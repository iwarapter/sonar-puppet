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
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.Trivia;
import org.apache.commons.lang.StringUtils;

public class CommentContainsPatternChecker {
  private final PuppetCheckVisitor check;
  private final String pattern;
  private final String message;

  public CommentContainsPatternChecker(PuppetCheckVisitor check, String pattern, String message) {
    this.check = check;
    this.pattern = pattern;
    this.message = message;
  }

  public void visitToken(Token token) {
    for (Trivia trivia : token.getTrivia()) {
      String comment = trivia.getToken().getOriginalValue();
      if (StringUtils.containsIgnoreCase(comment, pattern)) {
        String[] lines = comment.split("\r\n?|\n");

        for (int i = 0; i < lines.length; i++) {
          if (StringUtils.containsIgnoreCase(lines[i], pattern) && !isLetterAround(lines[i], pattern)) {
            check.addIssue(trivia.getToken().getLine() + i, check, message);
          }
        }
      }
    }
  }

  private static boolean isLetterAround(String line, String pattern) {
    int start = StringUtils.indexOfIgnoreCase(line, pattern);
    int end = start + pattern.length();

    boolean pre = start > 0 ? Character.isLetter(line.charAt(start - 1)) : false;
    boolean post = end < line.length() - 1 ? Character.isLetter(line.charAt(end)) : false;

    return pre || post;
  }

}
