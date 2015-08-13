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
import org.sonar.squidbridge.recognizer.*;
import org.sonar.sslr.parser.LexerlessGrammar;

@Rule(
  key = "CommentedOutCode",
  name = "Sections of code should not be commented out",
  priority = Priority.MAJOR,
  tags = {Tags.CONFUSING})
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.UNDERSTANDABILITY)
@SqaleConstantRemediation("5min")
@ActivatedByDefault
public class CommentedOutCodeCheck extends SquidCheck<LexerlessGrammar> implements AstAndTokenVisitor {

  private static final double THRESHOLD = 0.9;

  private static boolean isClassOrDefineFound = false;

  private final CodeRecognizer codeRecognizer = new CodeRecognizer(THRESHOLD, new PuppetRecognizer());
  private final Pattern regexpToDivideStringByLine = Pattern.compile("(\r?\n)|(\r)");

  @Override
  public void visitToken(Token token) {
    if (!isClassOrDefineFound) {
      // Only start looking for commented-out code after the first class or define to avoid false positives on documentation
      if ("class".equals(token.getValue()) || "define".equals(token.getValue())) {
        isClassOrDefineFound = true;
        return;
      }
    } else {
      Trivia previousTrivia = null;
      for (Trivia trivia : token.getTrivia()) {
        checkTrivia(previousTrivia, trivia);
        previousTrivia = trivia;
      }
    }
  }

  private void checkTrivia(Trivia previousTrivia, Trivia trivia) {
    if (trivia.getToken().getLine() != 1) {
      if (isInlineComment(trivia)) {
        if (isCommentedCode(getContext().getCommentAnalyser().getContents(trivia.getToken().getValue())) && !previousLineIsCommentedCode(trivia, previousTrivia)) {
          reportIssue(trivia.getToken().getLine());
        }
      } else {
        String[] lines = regexpToDivideStringByLine.split(getContext().getCommentAnalyser().getContents(trivia.getToken().getOriginalValue()));
        for (int lineOffset = 0; lineOffset < lines.length; lineOffset++) {
          if (isCommentedCode(lines[lineOffset])) {
            reportIssue(trivia.getToken().getLine() + lineOffset);
            break;
          }
        }
      }
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

  private boolean isInlineComment(Trivia trivia) {
    return trivia.getToken().getValue().startsWith("#") || trivia.getToken().getValue().startsWith("//");
  }

  private static class PuppetRecognizer implements LanguageFootprint {

    @Override
    public Set<Detector> getDetectors() {
      return ImmutableSet.of(
        new EndWithDetector(0.95, '}', ';', '{'),
        new EndWithDetector(0.75, ','),
        new KeywordsDetector(0.3, PuppetKeyword.keywordValues()),
        new ContainsDetector(0.95, "=~", "=>", "+>", "+>", ">=", ">>", "<=", "<<|", "<-", "<~", "<|", "<<", "!~", "!=", "|>>", "|>", "->", "~>"),
        new ContainsDetector(0.95, "include(", "include (", "include '", "include \""),
        new ContainsDetector(0.95, "require(", "require (", "require '", "require \""),
        new ContainsDetector(0.95, "contain(", "contain (", "contain '", "contain \""),
        new ContainsDetector(0.95, "realize(", "realize (", "realize '", "realize \""),
        new ContainsDetector(0.95, "tag(", "tag (", "tag '", "tag \""),
        new ContainsDetector(0.95, "debug(", "debug (", "debug '", "debug \""),
        new ContainsDetector(0.95, "info(", "info (", "info '", "info \""),
        new ContainsDetector(0.95, "notice(", "notice (", "notice '", "notice \""),
        new ContainsDetector(0.95, "warning(", "warning (", "warning '", "warning \""),
        new ContainsDetector(0.95, "err(", "err (", "err '", "err \""),
        new ContainsDetector(0.95, "fail(", "fail (", "fail '", "fail \""),
        new ContainsDetector(0.95, "crit(", "crit ("),
        new ContainsDetector(0.95, "defined(", "defined ("),
        new ContainsDetector(0.95, "digest(", "digest ("),
        new ContainsDetector(0.95, "each(", "each ("),
        new ContainsDetector(0.95, "emerg(", "emerg ("),
        new ContainsDetector(0.95, "epp(", "epp ("),
        new ContainsDetector(0.95, "file(", "file ("),
        new ContainsDetector(0.95, "filter(", "filter ("),
        new ContainsDetector(0.95, "generate(", "generate ("),
        new ContainsDetector(0.95, "hiera(", "hiera ("),
        new ContainsDetector(0.95, "lookup(", "lookup ("),
        new ContainsDetector(0.95, "map(", "map ("),
        new ContainsDetector(0.95, "match(", "match ("),
        new ContainsDetector(0.95, "md5(", "md5 ("),
        new ContainsDetector(0.95, "reduce(", "reduce ("),
        new ContainsDetector(0.95, "regsubst(", "regsubst ("),
        new ContainsDetector(0.95, "require(", "require ("),
        new ContainsDetector(0.95, "scanf(", "scanf ("),
        new ContainsDetector(0.95, "sha1(", "sha1 ("),
        new ContainsDetector(0.95, "shellquote(", "shellquote ("),
        new ContainsDetector(0.95, "slice(", "slice ("),
        new ContainsDetector(0.95, "split(", "split ("),
        new ContainsDetector(0.95, "sprintf(", "sprintf ("),
        new ContainsDetector(0.95, "tagged(", "tagged ("),
        new ContainsDetector(0.95, "template(", "template ("),
        new ContainsDetector(0.95, "versioncmp(", "versioncmp ("),
        new ContainsDetector(0.95, "with(", "with ("),
        new ContainsDetector(0.95, "assert_type(", "assert_type ("),
        new ContainsDetector(0.95, "create_resources(", "create_resources ("),
        new ContainsDetector(0.95, "hiera_array(", "hiera_array ("),
        new ContainsDetector(0.95, "hiera_include(", "hiera_include ("),
        new ContainsDetector(0.95, "hiera_include(", "hiera_include ("),
        new ContainsDetector(0.95, "inline_epp(", "inline_epp ("),
        new ContainsDetector(0.95, "inline_template(", "inline_template (")
        );
    }
  }

}
