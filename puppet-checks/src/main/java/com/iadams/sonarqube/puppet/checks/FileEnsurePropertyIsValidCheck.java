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
import com.iadams.sonarqube.puppet.api.PuppetTokenType;
import com.sonar.sslr.api.AstNode;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

@Rule(
  key = "FileEnsurePropertyIsValid",
  priority = Priority.CRITICAL,
  name = "\"ensure\" attribute of \"file\" resource should be valid",
  tags = {Tags.BUG})
@ActivatedByDefault
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.INSTRUCTION_RELIABILITY)
@SqaleConstantRemediation("10min")
public class FileEnsurePropertyIsValidCheck extends PuppetCheckVisitor {

  private static final String ACCEPTED_NAMES = "present|absent|file|directory|link";

  @Override
  public void init() {
    subscribeTo(PuppetGrammar.PARAM);
  }

  @Override
  public void visitNode(AstNode paramNode) {
    AstNode expressionNode = paramNode.getFirstChild(PuppetGrammar.EXPRESSION);
    if ("ensure".equals(paramNode.getFirstChild(PuppetGrammar.PARAM_NAME).getTokenValue())
      && "file".equalsIgnoreCase(paramNode.getFirstAncestor(PuppetGrammar.RESOURCE, PuppetGrammar.RESOURCE_OVERRIDE, PuppetGrammar.COLLECTION).getTokenValue())) {
      checkEnsureValidString(expressionNode);
      checkEnsureValidName(expressionNode);
    }
  }

  private void checkEnsureValidString(AstNode expressionNode) {
    if (expressionNode.getFirstChild(PuppetTokenType.SINGLE_QUOTED_STRING_LITERAL, PuppetTokenType.DOUBLE_QUOTED_STRING_LITERAL) != null) {
      String value = expressionNode.getFirstChild(PuppetTokenType.SINGLE_QUOTED_STRING_LITERAL, PuppetTokenType.DOUBLE_QUOTED_STRING_LITERAL).getTokenValue();
      String unquotedValue = value.substring(1, value.length() - 1);
      if (!CheckStringUtils.containsVariable(unquotedValue) && !unquotedValue.matches(ACCEPTED_NAMES)) {
        addIssue(expressionNode, this, "Fix the invalid \"ensure\" property.");
      }
    }
  }

  private void checkEnsureValidName(AstNode expressionNode) {
    if (expressionNode.getFirstChild(PuppetTokenType.NAME) != null
      && !expressionNode.getFirstChild(PuppetTokenType.NAME).getTokenValue().matches(ACCEPTED_NAMES)) {
      addIssue(expressionNode, this, "Fix the invalid \"ensure\" property.");
    }
  }

}
