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

class DuplicateConditionCheckSpec extends Specification {

  def "validate check"() {
    given:
    SourceFile file = PuppetAstScanner.scanSingleFile(
      new File("src/test/resources/checks/same_conditions.pp"),
      new DuplicateConditionCheck());

    expect:
    CheckMessagesVerifier.verify(file.getCheckMessages())
      .next().atLine(9).withMessage("This branch duplicates the one on line 3.")
      .next().atLine(21).withMessage("This branch duplicates the one on line 17.")
      .next().atLine(34).withMessage("This branch duplicates the one on line 27.")
      .next().atLine(49).withMessage("This branch duplicates the one on line 46.")
      .next().atLine(73).withMessage("This branch duplicates the one on line 71.")
      .next().atLine(81).withMessage("This branch duplicates the one on line 78.")
      .next().atLine(93).withMessage("This branch duplicates the one on line 89.")
      .next().atLine(103).withMessage("This branch duplicates the one on line 99.")
      .next().atLine(112).withMessage("This branch duplicates the one on line 109.")
      .next().atLine(115).withMessage("This branch duplicates the one on line 109.")
      .next().atLine(127).withMessage("This branch duplicates the one on line 124.")
      .next().atLine(130).withMessage("This branch duplicates the one on line 124.")
      .next().atLine(141).withMessage("This branch duplicates the one on line 139.")
      .next().atLine(142).withMessage("This branch duplicates the one on line 140.")
      .noMore();
  }
}
