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
import com.iadams.sonarqube.puppet.api.PuppetPunctuator;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;
import org.sonar.squidbridge.checks.SquidCheck;

@Rule(
  key = "TrailingCommas",
  priority = Priority.MINOR,
  name = "A trailing comma should be added after each resource attribute, parameter definition, hash pair and selector case",
  tags = Tags.CONVENTION)
@ActivatedByDefault
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.READABILITY)
@SqaleConstantRemediation("5min")
public class TrailingCommasCheck extends SquidCheck<Grammar> {

  @Override
  public void init() {
    subscribeTo(
      PuppetGrammar.HASH_PAIRS,
      PuppetGrammar.PARAMS,
      PuppetGrammar.ANY_PARAMS,
      PuppetGrammar.SINTVALUES,
      PuppetGrammar.ARGUMENTS);
  }

  @Override
  public void visitNode(AstNode node) {
    if (node.is(PuppetGrammar.PARAMS) && node.getChildren(PuppetGrammar.PARAM).size() != node.getChildren(PuppetPunctuator.COMMA).size()
      || node.is(PuppetGrammar.ANY_PARAMS) && node.getChildren(PuppetGrammar.PARAM, PuppetGrammar.ADD_PARAM).size() != node.getChildren(PuppetPunctuator.COMMA).size()
      || node.is(PuppetGrammar.HASH_PAIRS) && node.getChildren(PuppetGrammar.HASH_PAIR).size() != node.getChildren(PuppetPunctuator.COMMA).size()
      || node.is(PuppetGrammar.SINTVALUES) && node.getChildren(PuppetGrammar.SELECTVAL).size() != node.getChildren(PuppetPunctuator.COMMA).size()
      || node.is(PuppetGrammar.ARGUMENTS) && node.getChildren(PuppetGrammar.ARGUMENT).size() != node.getChildren(PuppetPunctuator.COMMA).size()) {
      getContext().createLineViolation(this, "Add the missing trailing comma.", node);
    }
  }

}
