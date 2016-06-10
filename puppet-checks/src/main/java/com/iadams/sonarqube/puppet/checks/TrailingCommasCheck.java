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

import com.iadams.sonarqube.puppet.PuppetCheckVisitor;
import com.iadams.sonarqube.puppet.api.PuppetGrammar;
import com.iadams.sonarqube.puppet.api.PuppetPunctuator;
import com.sonar.sslr.api.AstNode;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

@Rule(
  key = "TrailingCommas",
  priority = Priority.MINOR,
  name = "A trailing comma should be added after each resource attribute, parameter definition, hash pair and selector case",
  tags = Tags.CONVENTION)
@ActivatedByDefault
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.LOGIC_CHANGEABILITY)
@SqaleConstantRemediation("2min")
public class TrailingCommasCheck extends PuppetCheckVisitor {

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
    checkTrailingCommas(node, PuppetGrammar.PARAMS, PuppetGrammar.PARAM);
    checkTrailingCommas(node, PuppetGrammar.ANY_PARAMS, PuppetGrammar.PARAM, PuppetGrammar.ADD_PARAM);
    checkTrailingCommas(node, PuppetGrammar.HASH_PAIRS, PuppetGrammar.HASH_PAIR);
    checkTrailingCommas(node, PuppetGrammar.SINTVALUES, PuppetGrammar.SELECTVAL);
    checkTrailingCommas(node, PuppetGrammar.ARGUMENTS, PuppetGrammar.ARGUMENT);
  }

  private void checkTrailingCommas(AstNode node, PuppetGrammar parentType, PuppetGrammar... childType) {
    if (hasNotAllTrailingCommas(node, parentType, childType)) {
      if (node.is(PuppetGrammar.PARAMS, PuppetGrammar.PARAMS)) {
        if (hasNotTrailingSemiColon(node)) {
          createIssue(node, childType);
        }
      } else {
        createIssue(node, childType);
      }
    }
  }

  private boolean hasNotAllTrailingCommas(AstNode node, PuppetGrammar parentType, PuppetGrammar... childType) {
    return node.is(parentType) && node.getChildren(childType).size() != node.getChildren(PuppetPunctuator.COMMA).size();
  }

  private boolean hasNotTrailingSemiColon(AstNode node) {
    return node.getNextAstNode() == null || !node.getNextAstNode().is(PuppetPunctuator.SEMIC);
  }

  private void createIssue(AstNode node, PuppetGrammar... childType) {
    addIssue(node.getChildren(childType).get(node.getChildren(childType).size() - 1), this, "Add the missing trailing comma.");
  }

}
