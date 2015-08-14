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

class ComplexExpressionCheckSpec extends Specification {

  def "validate check with default parameter"() {
    given:
    SourceFile file = PuppetAstScanner.scanSingleFile(
      new File("src/test/resources/checks/complex_expression.pp"),
      new ComplexExpressionCheck());

    expect:
    CheckMessagesVerifier.verify(file.getCheckMessages())
      .next().atLine(2).withCost(1).withMessage("Reduce the number of boolean operators. This condition contains 4 boolean operators, 1 more than the 3 maximum.")
      .next().atLine(5).withCost(1).withMessage("Reduce the number of boolean operators. This condition contains 4 boolean operators, 1 more than the 3 maximum.")
      .next().atLine(8).withCost(1).withMessage("Reduce the number of boolean operators. This condition contains 4 boolean operators, 1 more than the 3 maximum.")
      .next().atLine(10).withCost(1).withMessage("Reduce the number of boolean operators. This condition contains 4 boolean operators, 1 more than the 3 maximum.")
      .next().atLine(12).withCost(2).withMessage("Reduce the number of boolean operators. This condition contains 5 boolean operators, 2 more than the 3 maximum.")
      .noMore();
  }

  def "validate check wit custom parameter"() {
    given:
    ComplexExpressionCheck check = new ComplexExpressionCheck();
    check.setMaxNumberOfBooleanOperators(4);
    SourceFile file = PuppetAstScanner.scanSingleFile(new File("src/test/resources/checks/complex_expression.pp"), check);

    expect:
    CheckMessagesVerifier.verify(file.getCheckMessages())
      .next().atLine(12).withCost(1).withMessage("Reduce the number of boolean operators. This condition contains 5 boolean operators, 1 more than the 4 maximum.")
      .noMore();
  }

}
