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

import java.util.regex.Pattern;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

@Rule(
  key = "FaultyQuoteUsage",
  priority = Priority.MINOR,
  name = "Single and double quotes should be properly used",
  tags = Tags.CONVENTION)
@ActivatedByDefault
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.READABILITY)
@SqaleConstantRemediation("2min")
public class FaultyQuoteUsageCheck extends PuppetCheckVisitor {

  @Override
  public void init() {
    subscribeTo(PuppetTokenType.DOUBLE_QUOTED_STRING_LITERAL, PuppetTokenType.SINGLE_QUOTED_STRING_LITERAL);
  }

  @Override
  public void visitNode(AstNode node) {
    checkDoubleQuotedString(node);
    checkSingleQuotedString(node);
  }

  private void checkDoubleQuotedString(AstNode node) {
    if (node.is(PuppetTokenType.DOUBLE_QUOTED_STRING_LITERAL)) {
      String stringWithoutQuotes = node.getTokenValue().substring(1, node.getTokenValue().length() - 1);
      if (!CheckStringUtils.containsVariable(stringWithoutQuotes) && !CheckStringUtils.containsSpecialCharacter(stringWithoutQuotes)) {
        addIssue(node, this, "Surround the string with single quotes instead of double quotes.");
      } else if (!node.getParent().is(PuppetGrammar.RESOURCE_NAME) && CheckStringUtils.containsOnlyVariable(stringWithoutQuotes)) {
        addIssue(node, this, "Remove quotes surrounding this variable.");
      }
    }
  }

  private void checkSingleQuotedString(AstNode node) {
    if (node.is(PuppetTokenType.SINGLE_QUOTED_STRING_LITERAL)) {
      String stringWithoutQuotes = node.getTokenValue().substring(1, node.getTokenValue().length() - 1);
      if (Pattern.compile("\\\\'").matcher(stringWithoutQuotes).find()) {
        addIssue(node, this, "Surround the string with double quotes instead of single quotes and do not escape single quotes inside this string.");
      }
    }
  }

}
