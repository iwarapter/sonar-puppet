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
import java.util.List;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;
import org.sonar.sslr.ast.AstSelect;

@Rule(
  key = "S1066",
  priority = Priority.MAJOR,
  name = "Collapsible \"if\" statements should be merged",
  tags = {Tags.CONFUSING})
@ActivatedByDefault
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.READABILITY)
@SqaleConstantRemediation("5min")
public class CollapsibleIfStatementsCheck extends PuppetCheckVisitor {

  @Override
  public void init() {
    subscribeTo(PuppetGrammar.IF_STMT, PuppetGrammar.ELSIF_STMT);
  }

  @Override
  public void visitNode(AstNode node) {
    AstNode singleIfChild = singleIfChild(node);
    if(singleIfChild != null && !hasElseOrElsif(singleIfChild)){
      addIssue(singleIfChild, this, "Merge this \"if\" statement with the enclosing one.");
    }
  }

  private boolean hasElseOrElsif(AstNode ifNode){
    return ifNode.hasDirectChildren(PuppetGrammar.ELSIF_STMT, PuppetGrammar.ELSE_STMT);
  }

  private AstNode singleIfChild(AstNode statement){
    List<AstNode> statements = statement.getChildren(PuppetGrammar.STATEMENT);

    if(statements.size() == 1){
      AstSelect nestedIf = statements.get(0).select()
        .children(PuppetGrammar.COMPOUND_STMT)
        .children(PuppetGrammar.IF_STMT);
      if(nestedIf.size() == 1){
        return nestedIf.get(0);
      }
    }
    return null;
  }
}
