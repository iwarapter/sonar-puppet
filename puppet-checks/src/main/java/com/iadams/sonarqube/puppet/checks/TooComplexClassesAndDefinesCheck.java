/*
 * SonarQube Puppet Plugin
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Iain Adams and David RACODON
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.iadams.sonarqube.puppet.checks;

import com.google.common.annotations.VisibleForTesting;
import com.iadams.sonarqube.puppet.api.PuppetGrammar;
import com.iadams.sonarqube.puppet.api.PuppetMetric;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleLinearWithOffsetRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;
import org.sonar.squidbridge.api.SourceClass;
import org.sonar.squidbridge.api.SourceFile;
import org.sonar.squidbridge.checks.ChecksHelper;
import org.sonar.squidbridge.checks.SquidCheck;

@Rule(
  key = "TooComplexClassesAndDefines",
  priority = Priority.MAJOR,
  name = "Classes and defines should not be too complex",
  tags = Tags.BRAIN_OVERLOAD)
@ActivatedByDefault
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.ARCHITECTURE_CHANGEABILITY)
@SqaleLinearWithOffsetRemediation(coeff = "5min", offset = "30min", effortToFixDescription = "per complexity point above the threshold")
public class TooComplexClassesAndDefinesCheck extends SquidCheck<Grammar> {

  public static final int DEFAULT_MAX_COMPLEXITY = 50;

  @RuleProperty(
    key = "max",
    defaultValue = "" + DEFAULT_MAX_COMPLEXITY,
    description = "Maximum points of complexity")
  private int max = DEFAULT_MAX_COMPLEXITY;

  @Override
  public void init() {
    subscribeTo(PuppetGrammar.DEFINITION, PuppetGrammar.CLASSDEF);
  }

  @Override
  public void leaveNode(AstNode node) {
    SourceFile sourceClass = (SourceFile) getContext().peekSourceCode();
    int complexity = ChecksHelper.getRecursiveMeasureInt(sourceClass, PuppetMetric.COMPLEXITY);

    if (complexity > max) {
      String nodeType = node.is(PuppetGrammar.CLASSDEF) ? "class" : "define";
      getContext().createLineViolation(this,
        "The complexity of this " + nodeType + " is {0} which is greater than {1} authorized. Split this " + nodeType + ".",
        node, complexity, max);
    }
  }

  @VisibleForTesting
  public void setMax(int max) {
    this.max = max;
  }

}
