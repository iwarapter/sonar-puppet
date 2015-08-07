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
import org.apache.commons.lang.StringUtils;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;
import org.sonar.squidbridge.checks.SquidCheck;

@Rule(
  key = "InheritsAcrossNamespace",
  priority = Priority.MINOR,
  name = "Classes should not inherit across namespaces",
  tags = {Tags.CONVENTION, Tags.PITFALL})
@ActivatedByDefault
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.ARCHITECTURE_CHANGEABILITY)
@SqaleConstantRemediation("1h")
public class InheritsAcrossNamespaceCheck extends SquidCheck<Grammar> {

  @Override
  public void init() {
    subscribeTo(PuppetGrammar.CLASSDEF);
  }

  @Override
  public void visitNode(AstNode node) {
    if(node.hasDescendant(PuppetGrammar.CLASS_PARENT)){
      String inherited_module_name = node.getFirstDescendant(PuppetGrammar.CLASS_PARENT).getFirstChild().getNextSibling().getTokenValue();
      String class_module_name = node.getFirstDescendant(PuppetGrammar.CLASSNAME).getTokenValue();

      inherited_module_name = StringUtils.substringBefore(inherited_module_name, "::");
      class_module_name = StringUtils.substringBefore(class_module_name, "::");

      if(!inherited_module_name.equals(class_module_name)){
        getContext().createLineViolation(this, "Bang", node.getTokenLine());
      }
    }
  }
}
