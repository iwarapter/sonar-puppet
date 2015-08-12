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

class FaultyQuoteUsageCheckSpec extends Specification {

  private final static String MESSAGE_USE_SINGLE_QUOTES = "Surround the string with single quotes instead of double quotes.";
  private final static String MESSAGE_REMOVE_QUOTES = "Remove quotes surrounding this variable.";
  private final static String MESSAGE_USE_DOUBLE_QUOTES = "Surround the string with double quotes instead of single quotes and unescaped single quotes inside this string.";

  def "validate check"() {
    given:
    SourceFile file = PuppetAstScanner.scanSingleFile(
      new File("src/test/resources/checks/faulty_quote_usage.pp"),
      new FaultyQuoteUsageCheck());

    expect:
    CheckMessagesVerifier.verify(file.getCheckMessages())
      .next().atLine(2).withMessage(MESSAGE_USE_SINGLE_QUOTES)
      .next().atLine(3).withMessage(MESSAGE_USE_SINGLE_QUOTES)
      .next().atLine(4).withMessage(MESSAGE_USE_SINGLE_QUOTES)
      .next().atLine(5).withMessage(MESSAGE_REMOVE_QUOTES)
      .next().atLine(6).withMessage(MESSAGE_REMOVE_QUOTES)
      .next().atLine(11).withMessage(MESSAGE_USE_SINGLE_QUOTES)
      .next().atLine(14).withMessage(MESSAGE_REMOVE_QUOTES)
      .next().atLine(17).withMessage(MESSAGE_REMOVE_QUOTES)
      .next().atLine(21).withMessage(MESSAGE_USE_DOUBLE_QUOTES)
      .noMore();
  }

}