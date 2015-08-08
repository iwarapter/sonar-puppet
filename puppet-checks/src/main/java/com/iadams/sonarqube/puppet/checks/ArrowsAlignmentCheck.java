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
import com.iadams.sonarqube.puppet.api.PuppetTokenType;
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
  key = "ArrowsAlignment",
  priority = Priority.MINOR,
  name = "All arrows in attribute/value list should be aligned",
  tags = Tags.CONVENTION)
@ActivatedByDefault
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.READABILITY)
@SqaleConstantRemediation("5min")
public class ArrowsAlignmentCheck extends SquidCheck<Grammar> {

  private static final String MESSAGE = "Properly align arrows (arrows are not all placed at the same column).";
  private static final String MESSAGE_SPACE = "Properly align arrows (arrows are not all placed one space ahead of the longest attribute).";

  @Override
  public void init() {
    subscribeTo(PuppetGrammar.HASH, PuppetGrammar.PARAMS, PuppetGrammar.ANY_PARAMS, PuppetGrammar.SELECTOR);
  }

  @Override
  public void visitNode(AstNode node) {
    if (node.is(PuppetGrammar.ANY_PARAMS)) {
      checkAllArrowsAlignment(node);
    } else if (node.is(PuppetGrammar.HASH)) {
      checkHashRocketsAlignment(node, PuppetGrammar.HASH_PAIR, PuppetGrammar.KEY);
    } else if (node.is(PuppetGrammar.PARAMS)) {
      checkHashRocketsAlignment(node, PuppetGrammar.PARAM, PuppetGrammar.PARAM_NAME);
    } else if (node.is(PuppetGrammar.SELECTOR)) {
      checkHashRocketsAlignment(node, PuppetGrammar.SELECTVAL, PuppetGrammar.SELECTLHAND);
    }
  }

  private void checkHashRocketsAlignment(AstNode node, PuppetGrammar nodeTypeContainingArrow, PuppetGrammar nodeTypeBeforeArrow) {
    int arrowColumn = -1;
    for (AstNode nodeContainingArrow : node.getChildren(nodeTypeContainingArrow)) {
      if (arrowColumn == -1) {
        arrowColumn = nodeContainingArrow.getFirstChild(PuppetPunctuator.FARROW).getToken().getColumn();
      } else if (nodeContainingArrow.getFirstChild(PuppetPunctuator.FARROW).getToken().getColumn() != arrowColumn) {
        getContext().createLineViolation(this, MESSAGE, node);
        return;
      }
    }
    if (arrowColumn != -1) {
      checkHashRocketsAsLeftAsPossible(node, nodeTypeContainingArrow, nodeTypeBeforeArrow, arrowColumn);
    }
  }

  private void checkHashRocketsAsLeftAsPossible(AstNode node, PuppetGrammar nodeTypeContainingArrow, PuppetGrammar nodeTypeBeforeArrow, int arrowColumn) {
    int upperColumn = -1;
    int currentColumn;
    for (AstNode nodeContainingArrow : node.getChildren(nodeTypeContainingArrow)) {
      currentColumn = nodeContainingArrow.getFirstChild(nodeTypeBeforeArrow).getLastToken().getColumn()
        + nodeContainingArrow.getFirstChild(nodeTypeBeforeArrow).getTokenValue().length();
      if (currentColumn > upperColumn) {
        upperColumn = currentColumn;
      }
    }
    if (upperColumn != arrowColumn - 1) {
      getContext().createLineViolation(this, MESSAGE_SPACE, node);
    }
  }

  private void checkAllArrowsAlignment(AstNode node) {
    int arrowColumn = -1;
    for (AstNode paramNode : node.getChildren(PuppetGrammar.PARAM)) {
      if (arrowColumn == -1) {
        arrowColumn = paramNode.getFirstChild(PuppetPunctuator.FARROW).getToken().getColumn();
      } else if (paramNode.getFirstChild(PuppetPunctuator.FARROW).getToken().getColumn() != arrowColumn) {
        getContext().createLineViolation(this, MESSAGE, node);
        return;
      }
    }
    for (AstNode anyParamNode : node.getChildren(PuppetGrammar.ADD_PARAM)) {
      if (arrowColumn == -1) {
        arrowColumn = anyParamNode.getFirstChild(PuppetPunctuator.PARROW).getToken().getColumn();
      } else if (anyParamNode.getFirstChild(PuppetPunctuator.PARROW).getToken().getColumn() != arrowColumn) {
        getContext().createLineViolation(this, MESSAGE, node);
        return;
      }
    }

    if (arrowColumn != -1) {
      int upperColumn = -1;
      int currentColumn;
      for (AstNode paramNode : node.getChildren(PuppetGrammar.PARAM)) {
        currentColumn = paramNode.getFirstChild(PuppetGrammar.PARAM_NAME).getLastToken().getColumn() + paramNode.getFirstChild(PuppetGrammar.PARAM_NAME).getTokenValue().length();
        if (currentColumn > upperColumn) {
          upperColumn = currentColumn;
        }
      }
      for (AstNode paramNode : node.getChildren(PuppetGrammar.ADD_PARAM)) {
        currentColumn = paramNode.getFirstChild(PuppetTokenType.NAME).getLastToken().getColumn() + paramNode.getFirstChild(PuppetTokenType.NAME).getTokenValue().length();
        if (currentColumn > upperColumn) {
          upperColumn = currentColumn;
        }
      }
      if (upperColumn + 1 != arrowColumn) {
        getContext().createLineViolation(this, MESSAGE_SPACE, node);
      }
    }
  }

}
