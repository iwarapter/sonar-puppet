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

class QuotedEnumerableCheckSpec extends Specification {

  def "validate check"() {
    given:
    SourceFile file = PuppetAstScanner.scanSingleFile(
      new File("src/test/resources/checks/quoted_enumerable.pp"),
      new QuotedEnumerableCheck());

    expect:
    CheckMessagesVerifier.verify(file.getCheckMessages())
      .next().atLine(2).withMessage("Remove the quotes surrounding \"present\".")
      .next().atLine(3).withMessage("Remove the quotes surrounding 'present'.")
      .next().atLine(4).withMessage("Remove the quotes surrounding \"notice\".")
      .next().atLine(5).withMessage("Remove the quotes surrounding 'notice'.")
      .next().atLine(18).withMessage("Remove the quotes surrounding \"present\".")
      .next().atLine(19).withMessage("Remove the quotes surrounding 'present'.")
      .next().atLine(20).withMessage("Remove the quotes surrounding \"notice\".")
      .next().atLine(21).withMessage("Remove the quotes surrounding 'notice'.")
      .next().atLine(34).withMessage("Remove the quotes surrounding \"present\".")
      .next().atLine(35).withMessage("Remove the quotes surrounding 'present'.")
      .next().atLine(36).withMessage("Remove the quotes surrounding \"notice\".")
      .next().atLine(37).withMessage("Remove the quotes surrounding 'notice'.")
      .next().atLine(50).withMessage("Remove the quotes surrounding \"present\".")
      .next().atLine(51).withMessage("Remove the quotes surrounding 'present'.")
      .next().atLine(52).withMessage("Remove the quotes surrounding \"notice\".")
      .next().atLine(53).withMessage("Remove the quotes surrounding 'notice'.")
      .noMore();
  }

}
