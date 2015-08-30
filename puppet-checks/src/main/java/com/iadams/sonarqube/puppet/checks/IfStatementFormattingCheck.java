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

import com.google.common.base.Preconditions;
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
  key = "IfStatementFormatting",
  priority = Priority.MINOR,
  name = "All \"if\" statements should be formatted the same way",
  tags = {Tags.CONVENTION})
@ActivatedByDefault
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.READABILITY)
@SqaleConstantRemediation("2min")
public class IfStatementFormattingCheck extends PuppetCheckVisitor {

  @Override
  public void init() {
    subscribeTo(PuppetGrammar.IF_STMT, PuppetGrammar.ELSIF_STMT, PuppetGrammar.ELSE_STMT);
  }

  @Override
  public void visitNode(AstNode node) {
    checkOpeningCurlyBraceLastTokenOnTheLine(node.getFirstChild(PuppetPunctuator.LBRACE));
    checkClosingCurlyBraceOnlyTokenOnTheLine(node.getFirstChild(PuppetPunctuator.RBRACE));
    checkElsifOnSameLineAsClosingCurlyBrace(node);
    checkElseOnSameLineAsClosingCurlyBrace(node);
  }

  private void checkOpeningCurlyBraceLastTokenOnTheLine(AstNode node) {
    if (isOnSameLine(node, node.getNextAstNode())) {
      addIssue(node, this, "Move the code following the opening curly brace to the next line.");
    }
    if (!isOnSameLine(node, node.getPreviousAstNode().getLastChild() != null ? node.getPreviousAstNode().getLastChild() : node.getPreviousAstNode())) {
      addIssue(node, this, "Move the opening curly brace to the previous line.");
    }
  }

  private void checkClosingCurlyBraceOnlyTokenOnTheLine(AstNode node) {
    if (isOnSameLine(node, node.getPreviousAstNode())) {
      addIssue(node, this, "Move the closing curly brace to the next line.");
    }
  }

  private void checkElsifOnSameLineAsClosingCurlyBrace(AstNode node) {
    if (node.is(PuppetGrammar.ELSIF_STMT)) {
      AstNode previousClosingBrace = node.getPreviousSibling().is(PuppetGrammar.ELSIF_STMT) ? node.getPreviousSibling().getFirstChild(PuppetPunctuator.RBRACE) : node
        .getPreviousSibling();
      if (!isOnSameLine(node, previousClosingBrace)) {
        addIssue(node, this, "Move the \"elsif\" statement next to the previous closing curly brace.");
      }
    }
  }

  private void checkElseOnSameLineAsClosingCurlyBrace(AstNode node) {
    if (node.is(PuppetGrammar.ELSE_STMT)) {
      AstNode previousClosingBrace = node.getParent().getLastChild(PuppetGrammar.ELSIF_STMT) != null ? node.getParent().getLastChild(PuppetGrammar.ELSIF_STMT)
        .getFirstChild(PuppetPunctuator.RBRACE) : node.getParent().getFirstChild(PuppetPunctuator.RBRACE);
      if (!isOnSameLine(node, previousClosingBrace)) {
        addIssue(node, this, "Move the \"else\" statement next to the previous closing curly brace.");
      }
    }
  }

  private static boolean isOnSameLine(AstNode... nodes) {
    Preconditions.checkArgument(nodes.length > 1);
    int lineRef = nodes[0].getTokenLine();
    for (AstNode node : nodes) {
      if (node.getTokenLine() != lineRef) {
        return false;
      }
    }
    return true;
  }

}
