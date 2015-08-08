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
  key = "HashRocketsAlignment",
  priority = Priority.MINOR,
  name = "All hash rockets (=>) in attribute/value list should be aligned",
  tags = Tags.CONVENTION)
@ActivatedByDefault
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.READABILITY)
@SqaleConstantRemediation("5min")
public class HashRocketsAlignmentCheck extends SquidCheck<Grammar> {

  private static final String MESSAGE = "Properly align hash rockets (hash rockets are not all placed at the same column).";
  private static final String MESSAGE_SPACE = "Properly align hash rockets (hash rockets are not placed one space ahead of the longest attribute).";

  @Override
  public void init() {
    subscribeTo(PuppetGrammar.HASH, PuppetGrammar.PARAMS, PuppetGrammar.SELECTOR);
  }

  @Override
  public void visitNode(AstNode node) {
    checkHashRocketsAlignment(node, PuppetGrammar.HASH, PuppetGrammar.HASH_PAIR, PuppetGrammar.KEY);
    checkHashRocketsAlignment(node, PuppetGrammar.PARAMS, PuppetGrammar.PARAM, PuppetGrammar.PARAM_NAME);
    checkHashRocketsAlignment(node, PuppetGrammar.SELECTOR, PuppetGrammar.SELECTVAL, PuppetGrammar.SELECTLHAND);
  }

  private void checkHashRocketsAlignment(AstNode node, PuppetGrammar firstLevelChildNode, PuppetGrammar secondLevelChildNode, PuppetGrammar thirdLevelChildNode) {
    if (node.is(firstLevelChildNode)) {
      int hashRocketColumn = -1;
      for (AstNode thirdLevelNode : node.getChildren(secondLevelChildNode)) {
        if (hashRocketColumn == -1) {
          hashRocketColumn = thirdLevelNode.getFirstChild(PuppetPunctuator.FARROW).getToken().getColumn();
        } else if (thirdLevelNode.getFirstChild(PuppetPunctuator.FARROW).getToken().getColumn() != hashRocketColumn) {
          getContext().createLineViolation(this, MESSAGE, node);
          hashRocketColumn = -1;
          break;
        }
      }
      if (hashRocketColumn != -1) {
        checkHashRocketsAsLeftAsPossible(node, firstLevelChildNode, secondLevelChildNode, thirdLevelChildNode, hashRocketColumn);
      }
    }
  }

  private void checkHashRocketsAsLeftAsPossible(AstNode node, PuppetGrammar firstLevelChildNode, PuppetGrammar secondLevelChildNode, PuppetGrammar thirdLevelChildNode,
    int hashRocketColumn) {
    int upperColumn = -1;
    int currentColumn;
    if (node.is(firstLevelChildNode)) {
      for (AstNode node2 : node.getChildren(secondLevelChildNode)) {
        currentColumn = node2.getFirstChild(thirdLevelChildNode).getLastToken().getColumn() + node2.getFirstChild(thirdLevelChildNode).getTokenValue().length();
        if (currentColumn > upperColumn) {
          upperColumn = currentColumn;
        }
      }
    }
    if (upperColumn + 1 != hashRocketColumn) {
      getContext().createLineViolation(this, MESSAGE_SPACE, node);
    }
  }

}
