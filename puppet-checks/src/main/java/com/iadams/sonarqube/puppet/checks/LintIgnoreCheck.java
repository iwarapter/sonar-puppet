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
import com.sonar.sslr.api.AstAndTokenVisitor;
import com.sonar.sslr.api.Token;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

@Rule(
  key = "LintIgnore",
  name = "Useless \"lint:ignore\" and \"lint:endignore\" tags should be removed",
  priority = Priority.MINOR)
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.UNDERSTANDABILITY)
@SqaleConstantRemediation("1min")
public class LintIgnoreCheck extends PuppetCheckVisitor implements AstAndTokenVisitor {

  private static final String PATTERN_BEGIN = "lint:ignore";
  private static final String PATTERN_END = "lint:endignore";
  private static final String MESSAGE_BEGIN = "Remove this useless \"lint:ignore\" tag.";
  private static final String MESSAGE_END = "Remove this useless \"lint:endignore\" tag.";

  private final CommentContainsPatternChecker checkerBegin = new CommentContainsPatternChecker(this, PATTERN_BEGIN, MESSAGE_BEGIN);
  private final CommentContainsPatternChecker checkerEnd = new CommentContainsPatternChecker(this, PATTERN_END, MESSAGE_END);

  @Override
  public void visitToken(Token token) {
    checkerBegin.visitToken(token);
    checkerEnd.visitToken(token);
  }

}
