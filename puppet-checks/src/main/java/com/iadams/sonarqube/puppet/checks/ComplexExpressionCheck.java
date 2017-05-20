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
import com.iadams.sonarqube.puppet.PuppetCheckVisitor;
import com.iadams.sonarqube.puppet.api.PuppetGrammar;
import com.sonar.sslr.api.AstNode;

import java.text.MessageFormat;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleLinearWithOffsetRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

@Rule(
  key = "ComplexExpression",
  priority = Priority.MAJOR,
  name = "Expressions should not be too complex",
  tags = {Tags.CONFUSING})
@ActivatedByDefault
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.UNDERSTANDABILITY)
@SqaleLinearWithOffsetRemediation(coeff = "2min", offset = "5min", effortToFixDescription = "number of boolean operators beyond the limit")
public class ComplexExpressionCheck extends PuppetCheckVisitor {

  private static final int MAX_NUMBER_OF_BOOLEAN_OPERATORS = 3;

  @RuleProperty(
    key = "max",
    defaultValue = "" + MAX_NUMBER_OF_BOOLEAN_OPERATORS,
    description = "Maximum number of boolean operators")
  private int max = MAX_NUMBER_OF_BOOLEAN_OPERATORS;

  @Override
  public void init() {
    subscribeTo(PuppetGrammar.EXPRESSION);
  }

  @Override
  public void visitNode(AstNode node) {
    if (node.getDescendants(PuppetGrammar.BOOL_OPERATOR).size() > max) {
      addIssue(node.getFirstDescendant(PuppetGrammar.BOOL_OPERATOR),
        this,
        MessageFormat.format(
          "Reduce the number of boolean operators. This condition contains {0,number,integer} boolean operators, {1,number,integer} more than the {2,number,integer} maximum.",
          node.getDescendants(PuppetGrammar.BOOL_OPERATOR).size(),
          node.getDescendants(PuppetGrammar.BOOL_OPERATOR).size() - max,
                max),
        (double) node.getDescendants(PuppetGrammar.BOOL_OPERATOR).size() - max);

    }
  }

  @VisibleForTesting
  public void setMax(int max) {
    this.max = max;
  }

}
