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
import com.sonar.sslr.api.AstNode;

import java.util.regex.Pattern;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

import static com.iadams.sonarqube.puppet.api.PuppetTokenType.*;

@Rule(
  key = "FileModes",
  name = "File mode should be represented by a valid 4-digit octal value (rather than 3) or symbolically",
  priority = Priority.MAJOR,
  tags = {Tags.BUG})
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.FAULT_TOLERANCE)
@SqaleConstantRemediation("10min")
@ActivatedByDefault
public class FileModeCheck extends PuppetCheckVisitor {

  private static final String REGEX = "['|\"]?([0-7]{4}|([ugoa]*[-=+][-=+rstwxXugo]*)(,[ugoa]*[-=+][-=+rstwxXugo]*)*)['|\"]?";
  private static final Pattern PATTERN = Pattern.compile(REGEX);
  private static final String MESSAGE_OCTAL = "Set the file mode to a 4-digit octal value surrounded by single quotes.";
  private static final String MESSAGE_DOUBLE_QUOTES = "Replace double quotes by single quotes.";
  private static final String MESSAGE_INVALID = "Update the file mode to a valid value surrounded by single quotes.";

  @Override
  public void init() {
    subscribeTo(PuppetGrammar.RESOURCE, PuppetGrammar.RESOURCE_OVERRIDE);
  }

  @Override
  public void visitNode(AstNode node) {
    if (node.is(PuppetGrammar.RESOURCE)) {
      checkResourceInstance(node);
      checkResourceDefault(node);
    } else if (node.is(PuppetGrammar.RESOURCE_OVERRIDE)) {
      checkResourceOverride(node);
    }
  }

  private void checkResourceInstance(AstNode resourceNode) {
    if ("file".equals(resourceNode.getTokenValue())) {
      for (AstNode resourceInstNode : resourceNode.getChildren(PuppetGrammar.RESOURCE_INST)) {
        for (AstNode paramNode : resourceInstNode.getFirstChild(PuppetGrammar.PARAMS).getChildren(PuppetGrammar.PARAM)) {
          checkMode(paramNode);
        }
      }
    }
  }

  private void checkResourceDefault(AstNode resourceNode) {
    if ("File".equals(resourceNode.getTokenValue())) {
      for (AstNode paramNode : resourceNode.getFirstChild(PuppetGrammar.PARAMS).getChildren(PuppetGrammar.PARAM)) {
        checkMode(paramNode);
      }
    }
  }

  private void checkResourceOverride(AstNode resourceOverrideNode) {
    if ("File".equals(resourceOverrideNode.getTokenValue())) {
      for (AstNode paramNode : resourceOverrideNode.getFirstChild(PuppetGrammar.ANY_PARAMS).getChildren(PuppetGrammar.PARAM, PuppetGrammar.ADD_PARAM)) {
        checkMode(paramNode);
      }
    }
  }

  private void checkMode(AstNode paramNode) {
    if ("mode".equals(paramNode.getTokenValue())) {
      AstNode expressionNode = paramNode.getFirstChild(PuppetGrammar.EXPRESSION);
      if (expressionNode.getToken().getType().equals(OCTAL_INTEGER) || expressionNode.getToken().getType().equals(INTEGER)) {
        addIssue(expressionNode, this, MESSAGE_OCTAL);
      } else if (expressionNode.getToken().getType().equals(DOUBLE_QUOTED_STRING_LITERAL) && PATTERN.matcher(expressionNode.getTokenValue()).matches()) {
        addIssue(expressionNode, this, MESSAGE_DOUBLE_QUOTES);
      } else if (expressionNode.getToken().getType().equals(SINGLE_QUOTED_STRING_LITERAL) && !PATTERN.matcher(expressionNode.getTokenValue()).matches()
        || expressionNode.getToken().getType().equals(DOUBLE_QUOTED_STRING_LITERAL) && !PATTERN.matcher(expressionNode.getTokenValue()).matches()
        && !CheckStringUtils.containsVariable(expressionNode.getTokenValue())) {
        addIssue(expressionNode, this, MESSAGE_INVALID);
      }
    }
  }

}
