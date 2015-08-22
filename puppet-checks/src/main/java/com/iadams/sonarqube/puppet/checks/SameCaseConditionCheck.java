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
import com.iadams.sonarqube.puppet.api.PuppetPunctuator;
import com.sonar.sslr.api.AstNode;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nullable;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;
import org.sonar.sslr.ast.AstSelect;

@Rule(
  key = "SameCaseCondition",
  priority = Priority.CRITICAL,
  name = "Related \"cases\" in a \"case\" should not have the same condition",
  tags = {Tags.BUG, Tags.UNUSED, Tags.PITFALL})
@ActivatedByDefault
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.LOGIC_RELIABILITY)
@SqaleConstantRemediation("10min")
public class SameCaseConditionCheck extends PuppetCheckVisitor {

  @Override
  public void init() {
    subscribeTo(PuppetGrammar.CASE_STMT);
  }

  @Override
  public void visitNode(AstNode node) {
    List<AstNode> conditions = new ArrayList<>();
    for(AstNode matcher : node.getChildren(PuppetGrammar.CASE_MATCHER)){
      conditions.addAll(matcher.getFirstChild(PuppetGrammar.CASE_VALUES).getChildren(PuppetGrammar.SELECTLHAND));
    }

    findSameConditions(conditions);
  }

  private void findSameConditions(List<AstNode> conditions) {
    for (int i = 1; i < conditions.size(); i++) {
      checkCondition(conditions, i);
    }
  }

  private void checkCondition(List<AstNode> conditions, int index) {
    for (int j = 0; j < index; j++) {
      if (equalNodes(conditions.get(j), conditions.get(index))) {
        String message = String.format("This branch duplicates the one on line %s.", conditions.get(j).getToken().getLine());
        getContext().createLineViolation(this, message, conditions.get(index).getToken().getLine());
        return;
      }
    }
  }

  public static boolean equalNodes(AstNode node1, AstNode node2) {
    if (!node1.getType().equals(node2.getType()) || node1.getNumberOfChildren() != node2.getNumberOfChildren()) {
      return false;
    }

    if (node1.getNumberOfChildren() == 0) {
      return node1.getToken().getValue().equals(node2.getToken().getValue());
    }

    List<AstNode> children1 = node1.getChildren();
    List<AstNode> children2 = node2.getChildren();
    for (int i = 0; i < children1.size(); i++) {
      if (!equalNodes(children1.get(i), children2.get(i))) {
        return false;
      }
    }
    return true;
  }
}
