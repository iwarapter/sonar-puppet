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

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

@Rule(
  key = "ResourceDefaultFirst",
  name = "Resource defaults should be defined before the first resource declaration",
  priority = Priority.MAJOR,
  tags = Tags.CONFUSING)
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.UNDERSTANDABILITY)
@SqaleConstantRemediation("2min")
@ActivatedByDefault
public class ResourceDefaultFirstCheck extends PuppetCheckVisitor {

  @Override
  public void init() {
    subscribeTo(PuppetGrammar.FILE_INPUT);
  }

  @Override
  public void visitNode(AstNode node) {
    int lineResourceInstance = getFirstResourceInstanceLine(node);
    if (lineResourceInstance == -1) {
      return;
    }
    for (AstNode resourceNode : getResourceDefaultNodes(node)) {
      if (resourceNode.getTokenLine() > lineResourceInstance) {
        addIssue(resourceNode, this, "Move this resource default before the first resource declaration.");
      }
    }
  }

  private static List<AstNode> getResourceDefaultNodes(AstNode node) {
    List<AstNode> resourceDefaults = new ArrayList();
    for (AstNode resourceNode : node.getDescendants(PuppetGrammar.RESOURCE)) {
      if (resourceNode.getFirstChild(PuppetGrammar.RESOURCE_INST) == null) {
        resourceDefaults.add(resourceNode);
      }
    }
    return resourceDefaults;
  }

  private static int getFirstResourceInstanceLine(AstNode node) {
    return !node.getDescendants(PuppetGrammar.RESOURCE_INST).isEmpty() ? node.getDescendants(PuppetGrammar.RESOURCE_INST).get(0).getTokenLine() : -1;
  }

}
