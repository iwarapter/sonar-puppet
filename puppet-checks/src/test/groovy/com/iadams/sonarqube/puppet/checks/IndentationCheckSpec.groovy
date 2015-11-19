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

class IndentationCheckSpec extends Specification {

  def "validate check"() {
    given:
    SourceFile file = PuppetAstScanner.scanSingleFile(
      new File("src/test/resources/checks/indentation.pp"),
      new IndentationCheck());

    expect:
    CheckMessagesVerifier.verify(file.getCheckMessages())
      .next().atLine(3).withMessage("The following 1 lines should start on column 3.")
      .next().atLine(8).withMessage("The following 1 lines should start on column 5.")
      .next().atLine(13).withMessage("The following 1 lines should start on column 5.")
      .next().atLine(25).withMessage("Make this line start at column 7.")
      .next().atLine(27).withMessage("Make this line start at column 5.")
      .next().atLine(30).withMessage("Make this line start at column 5.")
      .next().atLine(33).withMessage("Make this line start at column 5.")
      .next().atLine(39).withMessage("Make this line start at column 3.")
      .next().atLine(44).withMessage("Make this line start at column 3.")
      .next().atLine(46).withMessage("Make this line start at column 5.")
      .next().atLine(52).withMessage("Make this line start at column 3.")
      .next().atLine(58).withMessage("The following 1 lines should start on column 3.")
      .next().atLine(63).withMessage("The following 1 lines should start on column 5.")
      .next().atLine(70).withMessage("The following 1 lines should start on column 3.")
      .next().atLine(77).withMessage("The following 1 lines should start on column 5.")
      .next().atLine(79).withMessage("Make this line start at column 3.")
      .next().atLine(87).withMessage("The following 1 lines should start on column 3.")
      .next().atLine(93).withMessage("The following 1 lines should start on column 3.")
      .next().atLine(100).withMessage("Make this line start at column 5.")
      .next().atLine(103).withMessage("Make this line start at column 3.")
      .next().atLine(108).withMessage("The following 1 lines should start on column 3.")
      .next().atLine(114).withMessage("Make this line start at column 3.")
      .next().atLine(117).withMessage("Make this line start at column 5.")
      .next().atLine(125).withMessage("The following 1 lines should start on column 5.")
      .next().atLine(132).withMessage("The following 1 lines should start on column 7.")
      .next().atLine(136).withMessage("The following 1 lines should start on column 5.")
      .next().atLine(141).withMessage("The following 2 lines should start on column 1.")
      .next().atLine(293).withMessage("The following 3 lines should start on column 3.")
      .next().atLine(300).withMessage("The following 1 lines should start on column 3.")
      .next().atLine(303).withMessage("Make this line start at column 3.")
      .noMore();
  }
}