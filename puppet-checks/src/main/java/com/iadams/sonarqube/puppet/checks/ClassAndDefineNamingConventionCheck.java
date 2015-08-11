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

import com.iadams.sonarqube.puppet.api.PuppetGrammar;
import com.sonar.sslr.api.AstNode;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;
import org.sonar.squidbridge.checks.SquidCheck;
import org.sonar.sslr.parser.LexerlessGrammar;

@Rule(
  key = "ClassAndDefineNamingConvention",
  name = "Classes and defines should follow a naming convention",
  priority = Priority.MINOR,
  tags = {Tags.CONVENTION})
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.READABILITY)
@SqaleConstantRemediation("30min")
@ActivatedByDefault
public class ClassAndDefineNamingConventionCheck extends SquidCheck<LexerlessGrammar> {

  private static final String FORMAT = "^(::)?([a-z][a-z0-9_]*::)*[a-z][a-z0-9_]*$";

  @Override
  public void init() {
    subscribeTo(PuppetGrammar.DEFINITION, PuppetGrammar.CLASSDEF);
  }

  @Override
  public void leaveNode(AstNode node) {
    if (!node.getFirstChild(PuppetGrammar.CLASSNAME).getTokenValue().matches(FORMAT)) {
      String nodeType = node.is(PuppetGrammar.DEFINITION) ? "define" : "class";
      getContext().createLineViolation(this,
        "Rename " + nodeType + " \"{0}\" to match the regular expression: " + FORMAT,
        node.getFirstChild(PuppetGrammar.CLASSNAME),
        node.getFirstChild(PuppetGrammar.CLASSNAME).getTokenValue());
    }
  }

}
