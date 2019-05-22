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

import com.google.common.base.Charsets
import com.google.common.collect.ImmutableList
import com.iadams.sonarqube.puppet.api.PuppetMetric
import com.sonar.sslr.api.Grammar
import org.sonar.squidbridge.AstScanner
import org.sonar.squidbridge.api.SourceFile
import org.sonar.squidbridge.api.SourceProject
import org.sonar.squidbridge.indexer.QueryByType
import spock.lang.Specification

class PuppetAstScannerSpec extends Specification {

// Sonarqube 7.7: java.lang.UnsupportedOperationException: Metric 'files' should not be computed by a Sensor
//  def "files"() {
//    given:
//    AstScanner<Grammar> scanner = PuppetAstScanner.create(new PuppetConfiguration(Charsets.UTF_8))
//    scanner.scanFiles(ImmutableList.of(new File("src/test/resources/metrics/lines_of_code.pp"), new File("src/test/resources/metrics/comments.pp")))
//    SourceProject project = (SourceProject) scanner.getIndex().search(new QueryByType(SourceProject.class)).iterator().next()
//
//    expect:
//    project.getInt(PuppetMetric.FILES) == 2
//  }

  def "comments"() {
    given:
    SourceFile file = PuppetAstScanner.scanSingleFile(new File("src/test/resources/metrics/comments.pp"))

    expect:
    file.getInt(PuppetMetric.COMMENT_LINES) == 1
    file.getNoSonarTagLines().contains(3)
  }

  def "lines"() {
    given:
    SourceFile file = PuppetAstScanner.scanSingleFile(new File("src/test/resources/metrics/lines.pp"))

    expect:
    file.getInt(PuppetMetric.LINES) == 5
  }

  def "lines of code"() {
    given:
    SourceFile file = PuppetAstScanner.scanSingleFile(new File("src/test/resources/metrics/lines_of_code.pp"))

    expect:
    file.getInt(PuppetMetric.LINES_OF_CODE) == 6
  }

  def "resources"() {
    given:
    SourceFile file = PuppetAstScanner.scanSingleFile(new File("src/test/resources/metrics/resources.pp"));

    expect:
    file.getInt(PuppetMetric.FUNCTIONS) == 5
  }

  def "classes"() {
    given:
    SourceFile file = PuppetAstScanner.scanSingleFile(new File("src/test/resources/metrics/classes.pp"));

    expect:
    file.getInt(PuppetMetric.CLASSES) == 2
  }

  def "statements"() {
    given:
    SourceFile file = PuppetAstScanner.scanSingleFile(new File("src/test/resources/metrics/statements.pp"));

    expect:
    file.getInt(PuppetMetric.STATEMENTS) == 12
  }

  def "complexity"() {
    given:
    SourceFile file = PuppetAstScanner.scanSingleFile(new File("src/test/resources/metrics/complexity.pp"));

    expect:
    file.getInt(PuppetMetric.COMPLEXITY) == 19
  }

}
