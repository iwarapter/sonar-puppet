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

class EmptyBlocksCheckSpec extends Specification {

  def "validate rule"() {
    given:
    SourceFile file = PuppetAstScanner.scanSingleFile(
      new File("src/test/resources/checks/empty_blocks.pp"),
      new EmptyBlocksCheck());

    expect:
    CheckMessagesVerifier.verify(file.getCheckMessages())
      .next().atLine(4).withMessage("Remove this empty \"case\" matcher or add a comment to explain why it is empty.")
      .next().atLine(20).withMessage("Remove this empty \"if\" statement.")
      .next().atLine(21).withMessage("Remove this empty \"elsif\" statement or add a comment to explain why it is empty.")
      .next().atLine(22).withMessage("Remove this empty \"else\" statement or add a comment to explain why it is empty.")
      .next().atLine(34).withMessage("Remove this empty \"if\" statement.")
      .next().atLine(44).withMessage("Remove this empty \"unless\" statement.")
      .next().atLine(48).withMessage("Remove this empty \"unless\" statement.")
      .next().atLine(52).withMessage("Remove this empty argument list.")
      .next().atLine(55).withMessage("Remove this empty argument list.")
      .next().atLine(66).withMessage("Remove this empty class.")
      .next().atLine(67).withMessage("Remove this empty define.")
      .next().atLine(70).withMessage("Remove this empty resource default statement.")
      .next().atLine(71).withMessage("Remove this empty resource override.")
      .next().atLine(72).withMessage("Remove this empty resource collector.")
      .noMore();
  }
}
