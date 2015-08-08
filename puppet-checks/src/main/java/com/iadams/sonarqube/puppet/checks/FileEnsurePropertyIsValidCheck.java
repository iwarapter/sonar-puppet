/**
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
  priority = Priority.MINOR,
  name = "\"ensure\" property of file resource should be valid",
  tags = { Tags.CONFUSING, Tags.CONVENTION})
@ActivatedByDefault
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.READABILITY)
@SqaleConstantRemediation("10min")
public class FileEnsurePropertyIsValidCheck extends SquidCheck<Grammar> {

  private static final String ACCEPTED_NAMES = "('|\")?(present|absent|false|file|directory|link)('|\")?";

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

  private void checkResourceInstance(AstNode resourceNode){
    if ("file".equals(resourceNode.getTokenValue())) {
      for(AstNode instNode : resourceNode.getChildren(PuppetGrammar.RESOURCE_INST)){
        for(AstNode paramNode : instNode.getChildren(PuppetGrammar.PARAM)){
          if(paramNode.getTokenValue().equals("ensure")){
            AstNode expression = paramNode.getFirstChild().getNextSibling().getNextSibling();
            if(CheckStringUtils.isNodeStringLiteral(expression)){
              if(!CheckStringUtils.containsOnlyVariable(expression.getTokenValue()) && !expression.getTokenValue().matches(ACCEPTED_NAMES) ) {
                getContext().createLineViolation(this, "Remove the file reference and use link instead.", expression, expression.getTokenValue());
              }
            }
          }
        }
      }
    }
  }

  private void checkResourceDefault(AstNode resourceNode) {
    if ("File".equals(resourceNode.getTokenValue())) {
      for (AstNode paramNode : resourceNode.getChildren(PuppetGrammar.PARAM)) {
        if(paramNode.getTokenValue().equals("ensure")){
          AstNode expression = paramNode.getFirstChild().getNextSibling().getNextSibling();
          if(CheckStringUtils.isNodeStringLiteral(expression)){
            if(!CheckStringUtils.containsOnlyVariable(expression.getTokenValue()) && !expression.getTokenValue().matches(ACCEPTED_NAMES) ) {
              getContext().createLineViolation(this, "Remove the file reference and use link instead.", expression, expression.getTokenValue());
            }
          }
        }
      }
    }
  }

  private void checkResourceOverride(AstNode resourceOverrideNode) {
    if ("File".equals(resourceOverrideNode.getTokenValue())) {
      for (AstNode paramNode : resourceOverrideNode.getChildren(PuppetGrammar.PARAM)) {
        if(paramNode.getTokenValue().equals("ensure")){
          AstNode expression = paramNode.getFirstChild().getNextSibling().getNextSibling();
          if(CheckStringUtils.isNodeStringLiteral(expression)){
            if(!CheckStringUtils.containsOnlyVariable(expression.getTokenValue()) && !expression.getTokenValue().matches(ACCEPTED_NAMES) ) {
              getContext().createLineViolation(this, "Remove the file reference and use link instead.", expression, expression.getTokenValue());
            }
          }
        }
      }
    }
  }
}
