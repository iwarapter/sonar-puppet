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

import com.google.common.collect.ImmutableSet;
import com.iadams.sonarqube.puppet.PuppetCommentAnalyser;
import com.iadams.sonarqube.puppet.api.PuppetKeyword;
import com.sonar.sslr.api.AstAndTokenVisitor;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.Trivia;
import java.util.Set;
import java.util.regex.Pattern;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;
import org.sonar.squidbridge.checks.SquidCheck;
import org.sonar.squidbridge.recognizer.CodeRecognizer;
import org.sonar.squidbridge.recognizer.Detector;
import org.sonar.squidbridge.recognizer.EndWithDetector;
import org.sonar.squidbridge.recognizer.KeywordsDetector;
import org.sonar.squidbridge.recognizer.LanguageFootprint;
import org.sonar.sslr.parser.LexerlessGrammar;

@Rule(
  key = "S125",
  priority = Priority.MAJOR,
  name = "Sections of code should not be \"commented out\"",
  tags = Tags.UNUSED)
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.UNDERSTANDABILITY)
@SqaleConstantRemediation("5min")
@ActivatedByDefault
public class CommentedOutCodeCheck extends SquidCheck<LexerlessGrammar> implements AstAndTokenVisitor {

  private static final double THRESHOLD = 0.9;

  private final CodeRecognizer codeRecognizer = new CodeRecognizer(THRESHOLD, new PuppetRecognizer());

  private static class PuppetRecognizer implements LanguageFootprint {

    @Override
    public Set<Detector> getDetectors() {
      return ImmutableSet.of(
        new EndWithDetector(0.95, '}', ';', '{'),
        new KeywordsDetector(0.3, PuppetKeyword.keywordValues()));
    }
  }

  @Override
  public void visitToken(Token token) {
    Trivia previousTrivia = null;

    for (Trivia trivia : token.getTrivia()) {
      checkTrivia(previousTrivia, trivia);
      previousTrivia = trivia;
    }
  }

  private void checkTrivia(Trivia previousTrivia, Trivia trivia) {
    if (isCommentedCode(getContext().getCommentAnalyser().getContents(trivia.getToken().getValue()))
      && !previousLineIsCommentedCode(trivia, previousTrivia)) {
      reportIssue(trivia.getToken().getLine());
    }
  }

  private void reportIssue(int line) {
    getContext().createLineViolation(this, "Remove this commented out code.", line);
  }

  private boolean previousLineIsCommentedCode(Trivia trivia, Trivia previousTrivia) {
    return previousTrivia != null && (trivia.getToken().getLine() == previousTrivia.getToken().getLine() + 1)
      && isCommentedCode(previousTrivia.getToken().getValue());
  }

  private boolean isCommentedCode(String line) {
    return codeRecognizer.isLineOfCode(line);
  }

}
