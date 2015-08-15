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
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;
import org.sonar.squidbridge.checks.SquidCheck;

@Rule(
  key = "EmptyBlocks",
  name = "Empty blocks of code should be removed",
  priority = Priority.MAJOR,
  tags = {Tags.PITFALL})
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.LOGIC_RELIABILITY)
@SqaleConstantRemediation("5min")
public class EmptyBlocksCheck extends SquidCheck<Grammar> {

  @Override
  public void init() {
    subscribeTo(
      PuppetGrammar.CLASSDEF,
      PuppetGrammar.DEFINITION,

      PuppetGrammar.RESOURCE,
      PuppetGrammar.RESOURCE_OVERRIDE,

      PuppetGrammar.CASE_MATCHER,
      PuppetGrammar.IF_STMT,
      PuppetGrammar.ELSEIF_STMT,
      PuppetGrammar.ELSE_STMT,
      PuppetGrammar.UNLESS_STMT);
  }

  @Override
  public void visitNode(AstNode node) {
    checkClassesAndDefines(node);
    checkResources(node);
    checkConditionalStatements(node);
  }

  private void checkClassesAndDefines(AstNode node) {
    if (node.is(PuppetGrammar.CLASSDEF, PuppetGrammar.DEFINITION)) {
      if (node.getFirstChild(PuppetGrammar.ARGUMENTS) == null
        && node.getFirstChild(PuppetGrammar.CLASSNAME).getNextAstNode().is(PuppetPunctuator.LPAREN)) {
        getContext().createLineViolation(this, "Remove this empty argument list.", node);
      }
      if (node.getFirstChild(PuppetGrammar.STATEMENT) == null) {
        String nodeType = node.is(PuppetGrammar.CLASSDEF) ? "class" : "define";
        getContext().createLineViolation(this, "Remove this empty " + nodeType + ".", node);
      }
    }
  }

  private void checkResources(AstNode node) {
    if (node.is(PuppetGrammar.RESOURCE)
      && node.getFirstChild(PuppetGrammar.RESOURCE_INST) == null
      && node.getFirstChild(PuppetGrammar.PARAMS).getChildren(PuppetGrammar.PARAM).isEmpty()) {
      getContext().createLineViolation(this, "Remove this empty resource default statement.", node);
    } else if (node.is(PuppetGrammar.RESOURCE_OVERRIDE)
      && node.getFirstChild(PuppetGrammar.ANY_PARAMS).getChildren(PuppetGrammar.PARAM, PuppetGrammar.ADD_PARAM).isEmpty()) {
      getContext().createLineViolation(this, "Remove this empty resource override.", node);
    }
  }

  private void checkConditionalStatements(AstNode node) {
    if (node.getFirstChild(PuppetGrammar.STATEMENT) == null) {
      if (node.is(PuppetGrammar.IF_STMT)) {
        getContext().createLineViolation(this, "Remove this empty \"if\" statement.", node);
      } else if (node.is(PuppetGrammar.UNLESS_STMT)) {
        getContext().createLineViolation(this, "Remove this empty \"unless\" statement.", node);
      } else if (!hasTrivia(node)) {
        if (node.is(PuppetGrammar.CASE_MATCHER)) {
          getContext().createLineViolation(this, "Remove this empty \"case\" matcher or add a comment to explain why it is empty.", node);
        } else if (node.is(PuppetGrammar.ELSEIF_STMT)) {
          getContext().createLineViolation(this, "Remove this empty \"elsif\" statement or add a comment to explain why it is empty.", node);
        } else if (node.is(PuppetGrammar.ELSE_STMT)) {
          getContext().createLineViolation(this, "Remove this empty \"else\" statement or add a comment to explain why it is empty.", node);
        }
      }
    }
  }

  private boolean hasTrivia(AstNode node) {
    if (node.getToken().hasTrivia()) {
      return true;
    }
    for (AstNode childNode : node.getChildren()) {
      if (childNode.getToken().hasTrivia()) {
        return true;
      }
    }
    return false;
  }

}
