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
package com.iadams.sonarqube.puppet.checks

import com.iadams.sonarqube.puppet.PuppetAstScanner
import org.sonar.squidbridge.api.SourceFile
import org.sonar.squidbridge.checks.CheckMessagesVerifier
import spock.lang.Specification

class FixmeTagPresenceCheckSpec extends Specification {

  private static final String MESSAGE = "Take the required action to fix the issue indicated by this comment.";
  private final FixmeTagPresenceCheck check = new FixmeTagPresenceCheck();

  def "file should contain some FIXME tags"() {
    given:
    SourceFile file = PuppetAstScanner.scanSingleFile(new File("src/test/resources/checks/FixmeTagPresence.pp"), check);

    expect:
    CheckMessagesVerifier.verify(file.getCheckMessages())
      .next().atLine(1).withMessage(MESSAGE)
      .next().atLine(2).withMessage(MESSAGE)
      .next().atLine(4).withMessage(MESSAGE)
      .next().atLine(5).withMessage(MESSAGE)
      .next().atLine(6).withMessage(MESSAGE)
      .next().atLine(7).withMessage(MESSAGE)
      .next().atLine(9).withMessage(MESSAGE)
      .next().atLine(12).withMessage(MESSAGE)
      .next().atLine(13).withMessage(MESSAGE)
      .next().atLine(14).withMessage(MESSAGE)
      .next().atLine(16).withMessage(MESSAGE)
      .next().atLine(17).withMessage(MESSAGE)
      .next().atLine(18).withMessage(MESSAGE)
      .next().atLine(19).withMessage(MESSAGE)
      .next().atLine(20).withMessage(MESSAGE)
      .next().atLine(23).withMessage(MESSAGE)
      .next().atLine(24).withMessage(MESSAGE)
      .next().atLine(25).withMessage(MESSAGE)
      .next().atLine(27).withMessage(MESSAGE)
      .next().atLine(28).withMessage(MESSAGE)
      .next().atLine(29).withMessage(MESSAGE)
      .next().atLine(30).withMessage(MESSAGE)
      .next().atLine(31).withMessage(MESSAGE)
      .noMore();
  }

  def "file should not contain any FIXME tags"() {
    given:
    SourceFile file = PuppetAstScanner.scanSingleFile(new File("src/test/resources/checks/EnsureOrdering.pp"), check);

    expect:
    CheckMessagesVerifier.verify(file.getCheckMessages()).noMore();
  }
}
