/*
 * Sonar Puppet Plugin
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Iain Adams
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
import com.iadams.sonarqube.puppet.lexer.PuppetLexer;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import java.util.regex.Pattern;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;
import org.sonar.squidbridge.checks.SquidCheck;

import static com.iadams.sonarqube.puppet.api.PuppetTokenType.DOUBLE_QUOTED_STRING_LITERAL;
import static com.iadams.sonarqube.puppet.api.PuppetTokenType.SINGLE_QUOTED_STRING_LITERAL;

@Rule(
  key = "FileModes",
  name = "File modes should be represented as 4 digits rather than 3 or symbolically.",
  priority = Priority.MINOR,
  tags = {Tags.CONVENTION})
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.FAULT_TOLERANCE)
@SqaleConstantRemediation("10min")
@ActivatedByDefault
public class FileModeCheck extends SquidCheck<Grammar> {

  private static final String REGEX = "['|\"]?([0-7]{4}|([ugoa]*[-=+][-=+rstwxXugo]*)(,[ugoa]*[-=+][-=+rstwxXugo]*)*)['|\"]?";
  private Pattern pattern = Pattern.compile(REGEX);

  @Override
  public void init() {
    subscribeTo(PuppetGrammar.RESOURCE);
  }

  @Override
  public void visitNode(AstNode node) {
    if(node.getTokenValue().equals("file")) {
      for (AstNode body : node.getDescendants(PuppetGrammar.RESOURCE_INST)) {
        for (AstNode name : body.getDescendants(PuppetGrammar.PARAM)) {
          if(name.getTokenValue().equals("mode")){
            checkMode(name.getFirstChild(PuppetGrammar.EXPRESSION));
          }
        }
      }
    }
  }

  private void checkMode(AstNode node){
	if(node.getToken().getType().equals(SINGLE_QUOTED_STRING_LITERAL) || node.getToken().getType().equals(DOUBLE_QUOTED_STRING_LITERAL)) {
      System.out.println(pattern.matcher(node.getTokenValue()).matches() + " " + node.getTokenValue());
      if (!pattern.matcher(node.getTokenValue()).matches()) {
        getContext().createLineViolation(this, "File modes should be represented as 4 digits rather than 3, to explicitly show that they are octal values.", node.getTokenLine());
      }
	}
  }
}