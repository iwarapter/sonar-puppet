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
package com.iadams.sonarqube.puppet

import spock.lang.Specification

import com.google.common.base.Charsets
import org.sonar.api.batch.fs.internal.DefaultInputFile
import org.sonar.api.batch.fs.internal.FileMetadata
import org.sonar.api.batch.sensor.highlighting.TypeOfText
import org.sonar.api.batch.sensor.internal.SensorContextTester

class PuppetHighlighterSpec extends Specification {

  private SensorContextTester context

  private File file

  def setup(){
    String dir = "src/test/resources/com/iadams/sonarqube/puppet"

    file = new File(dir + "/puppetHighlighter.pp")
    DefaultInputFile inputFile = new DefaultInputFile("moduleKey", file.getName())
      .initMetadata(new FileMetadata().readMetadata(file, Charsets.UTF_8))

    context = SensorContextTester.create(new File(dir))
    context.fileSystem().add(inputFile)

    PuppetHighlighter puppetHighligher = new PuppetHighlighter(context)
    PuppetAstScanner.scanSingleFile(file, puppetHighligher)
  }


  def "keywords"() {
    expect:
    //class ssh::client inherits workstation { }
    checkOnRange(7, 0, 5, TypeOfText.KEYWORD)
    checkOnRange(7, 19, 7, TypeOfText.KEYWORD)
    //class wordpress inherits apache { }
    checkOnRange(9, 0, 5, TypeOfText.KEYWORD)
    checkOnRange(9, 17, 7, TypeOfText.KEYWORD)
  }

  def "string literals"() {
    expect:
    //$variable = "this is a string"
    checkOnRange(4, 13, 17, TypeOfText.STRING)
    //$variable2 = "this is a string"
    checkOnRange(5, 14, 17, TypeOfText.STRING)
    //file { '/tmp/foo':
    checkOnRange(11, 8, 9, TypeOfText.STRING)
    //  purge => 'true',
    checkOnRange(12, 12, 5, TypeOfText.STRING)
  }

  def "comments"() {
    expect:
    checkOnRange(1, 1, 8, TypeOfText.COMMENT)
    checkOnRange(2, 1, 10, TypeOfText.COMMENT)
  }

  /**
   * Checks the highlighting of a range of columns.
   * The range is the columns of the token.
   */
  private void checkOnRange(int line, int firstColumn, int length, TypeOfText expectedTypeOfText) {
    // check that every column of the token is highlighted (and with the expected type)
    for (int column = firstColumn; column < firstColumn + length; column++) {
      checkInternal(line, column, expectedTypeOfText)
    }

    // check that the column before the token is not highlighted
    if (firstColumn != 1) {
      checkInternal(line, firstColumn - 1, null)
    }

    // check that the column after the token is not highlighted
    checkInternal(line, firstColumn + length, null)
  }

  private void checkInternal(int line, int column, TypeOfText expectedTypeOfText) {
    String componentKey = "moduleKey:" + file.getName()
    List<TypeOfText> foundTypeOfTexts = context.highlightingTypeAt(componentKey, line, column)

    int expectedNumberOfTypeOfText = expectedTypeOfText == null ? 0 : 1
    foundTypeOfTexts.size() == expectedNumberOfTypeOfText
    if (expectedNumberOfTypeOfText > 0) {
      foundTypeOfTexts.get(0) == expectedTypeOfText
    }
  }

}