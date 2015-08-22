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

import com.google.common.collect.ImmutableMap;
import com.iadams.sonarqube.puppet.PuppetCheckVisitor;
import com.iadams.sonarqube.puppet.api.PuppetGrammar;
import com.iadams.sonarqube.puppet.api.PuppetPunctuator;
import com.sonar.sslr.api.AstNode;

import java.util.Map;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

@Rule(
  key = "S1940",
  name = "Boolean checks should not be inverted",
  priority = Priority.MAJOR,
  tags = {Tags.PITFALL})
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.UNDERSTANDABILITY)
@SqaleConstantRemediation("2min")
public class BooleanInversionCheck extends PuppetCheckVisitor {

  private static final Map<String, String> OPERATORS = ImmutableMap.<String, String>builder()
    .put("==", "!=")
    .put("!=", "==")
    .put("<", ">=")
    .put(">", "<=")
    .put("<=", ">")
    .put(">=", "<")
    .build();

  @Override
  public void init() {
    subscribeTo(PuppetGrammar.UNARY_NOT_EXPRESSION);
  }

  @Override
  public void visitNode(AstNode node) {
    if (node.getFirstChild().getType() == PuppetPunctuator.NOT) {
      if (node.getFirstChild(PuppetGrammar.COMPARISON) != null) {
        AstNode expression = node.getFirstChild(PuppetGrammar.COMPARISON);
        String val = expression.getFirstChild(PuppetGrammar.COMP_OPERATOR).getTokenValue();
        addIssue(expression, this, "Use the opposite operator (\"" + OPERATORS.get(val) + "\") instead.");
      } else if (node.getFirstChild(PuppetGrammar.BOOL_EXPRESSION) != null) {
        addIssue(node.getFirstChild(PuppetGrammar.BOOL_EXPRESSION), this, "Invert all the operators of this boolean expression instead.");
      }
    }
  }
}
