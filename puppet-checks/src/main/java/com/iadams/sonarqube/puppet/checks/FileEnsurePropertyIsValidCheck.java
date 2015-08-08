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
  key = "FileEnsurePropertyIsValid",
  priority = Priority.CRITICAL,
  name = "\"ensure\" property of file resource should be valid",
  tags = {Tags.BUG})
@ActivatedByDefault
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.INSTRUCTION_RELIABILITY)
@SqaleConstantRemediation("10min")
public class FileEnsurePropertyIsValidCheck extends SquidCheck<Grammar> {

  private static final String ACCEPTED_NAMES = "present|absent|file|directory|link";

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
      for (AstNode instNode : resourceNode.getChildren(PuppetGrammar.RESOURCE_INST)) {
        for (AstNode paramNode : instNode.getFirstChild(PuppetGrammar.PARAMS).getChildren(PuppetGrammar.PARAM)) {
          checkEnsureValid(paramNode);
        }
      }
    }
  }

  private void checkResourceDefault(AstNode resourceNode) {
    if ("File".equals(resourceNode.getTokenValue())) {
      for (AstNode paramNode : resourceNode.getFirstChild(PuppetGrammar.PARAMS).getChildren(PuppetGrammar.PARAM)) {
        checkEnsureValid(paramNode);
      }
    }
  }

  private void checkResourceOverride(AstNode resourceOverrideNode) {
    if ("File".equals(resourceOverrideNode.getTokenValue())) {
      for (AstNode paramNode : resourceOverrideNode.getFirstChild(PuppetGrammar.ANY_PARAMS).getChildren(PuppetGrammar.PARAM)) {
        checkEnsureValid(paramNode);
      }
    }
  }

  private void checkEnsureValid(AstNode paramNode) {
    if (paramNode.getTokenValue().equals("ensure")) {
      AstNode expression = paramNode.getFirstChild(PuppetGrammar.EXPRESSION);
      if (expression.getFirstChild(PuppetTokenType.SINGLE_QUOTED_STRING_LITERAL, PuppetTokenType.DOUBLE_QUOTED_STRING_LITERAL) != null
        || expression.getFirstChild(PuppetTokenType.NAME) != null && !expression.getFirstChild(PuppetTokenType.NAME).getTokenValue().matches(ACCEPTED_NAMES)) {
        getContext().createLineViolation(this, "Fix the invalid \"ensure\" property.", paramNode);
      }
    }
  }

}
