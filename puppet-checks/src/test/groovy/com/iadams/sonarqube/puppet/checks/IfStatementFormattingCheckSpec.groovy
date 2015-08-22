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

class IfStatementFormattingCheckSpec extends Specification {

  def "validate rule"() {
    given:
    SourceFile file = PuppetAstScanner.scanSingleFile(
      new File("src/test/resources/checks/if_statement_formatting.pp"),
      new IfStatementFormattingCheck());

    expect:
    CheckMessagesVerifier.verify(file.getCheckMessages())
      .next().atLine(10).withMessage("Move the opening curly brace to the previous line.")
      .next().atLine(13).withMessage("Move the opening curly brace to the previous line.")
      .next().atLine(16).withMessage("Move the opening curly brace to the previous line.")
      .next().atLine(23).withMessage("Move the \"elsif\" statement next to the previous closing curly brace.")
      .next().atLine(26).withMessage("Move the \"else\" statement next to the previous closing curly brace.")
      .next().atLine(30).withMessage("Move the code following the opening curly brace to the next line.")
      .next().atLine(35).withMessage("Move the code following the opening curly brace to the next line.")
      .next().atLine(42).withMessage("Move the code following the opening curly brace to the next line.")
      .next().atLine(46).withMessage("Move the closing curly brace to the next line.")
      .next().atLine(47).withMessage("Move the closing curly brace to the next line.")
      .next().atLine(48).withMessage("Move the closing curly brace to the next line.")
      .next().atLine(71).withMessage("Move the \"else\" statement next to the previous closing curly brace.")
      .noMore();
  }
}
