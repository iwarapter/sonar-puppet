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
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

@Rule(
  key = "NoopUsage",
  priority = Priority.CRITICAL,
  name = "\"noop\" metaparameter should be used for test purpose only",
  tags = {Tags.PITFALL})
@ActivatedByDefault
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.INSTRUCTION_RELIABILITY)
@SqaleConstantRemediation("5min")
public class NoopUsageCheck extends PuppetCheckVisitor {

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
    for (AstNode instNode : resourceNode.getChildren(PuppetGrammar.RESOURCE_INST)) {
      for (AstNode paramNode : instNode.getFirstChild(PuppetGrammar.PARAMS).getChildren(PuppetGrammar.PARAM)) {
        checkNoopUsage(paramNode);
      }
    }
  }

  private void checkResourceDefault(AstNode resourceNode) {
    if (resourceNode.getChildren(PuppetGrammar.RESOURCE_INST).size() == 0) {
      for (AstNode paramNode : resourceNode.getFirstChild(PuppetGrammar.PARAMS).getChildren(PuppetGrammar.PARAM)) {
        checkNoopUsage(paramNode);
      }
    }
  }

  private void checkResourceOverride(AstNode resourceOverrideNode) {
    for (AstNode paramNode : resourceOverrideNode.getFirstChild(PuppetGrammar.ANY_PARAMS).getChildren(PuppetGrammar.PARAM)) {
      checkNoopUsage(paramNode);
    }
  }

  private void checkNoopUsage(AstNode paramNode) {
    if (paramNode.getTokenValue().equals("noop")) {
      addIssue(paramNode, this, "Remove this usage of the \"noop\" metaparameter.");
    }
  }

}
