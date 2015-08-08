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

import com.iadams.sonarqube.puppet.metrics.FileLinesVisitor
import com.sonar.sslr.api.Grammar
import org.sonar.api.batch.fs.InputFile
import org.sonar.api.batch.fs.internal.DefaultFileSystem
import org.sonar.api.batch.fs.internal.DefaultInputFile
import org.sonar.api.measures.CoreMetrics
import org.sonar.api.measures.FileLinesContext
import org.sonar.api.measures.FileLinesContextFactory
import org.sonar.squidbridge.SquidAstVisitor
import spock.lang.Specification

class FileLinesVisitorSpec extends Specification {

  static final File BASE_DIR = new File("src/test/resources/metrics")

  FileLinesContextFactory fileLinesContextFactory
  DefaultFileSystem fileSystem
  FileLinesContext fileLinesContext

  def setup() {
    fileLinesContextFactory = Mock()
    fileSystem = new DefaultFileSystem()
    fileLinesContext = Mock()
  }

  def "check metrics calculate correctly"() {
    when:

    File file = new File(BASE_DIR, "lines.pp")
    InputFile inputFile = new DefaultInputFile(file.getPath())

    fileSystem.add(inputFile)
    fileLinesContextFactory.createFor(inputFile) >> fileLinesContext

    SquidAstVisitor<Grammar> visitor = new FileLinesVisitor(fileLinesContextFactory, fileSystem);

    PuppetAstScanner.scanSingleFile(file, visitor);

    then:
    1 * fileLinesContext.setIntValue(CoreMetrics.NCLOC_DATA_KEY, 1, 0)
    1 * fileLinesContext.setIntValue(CoreMetrics.NCLOC_DATA_KEY, 2, 0)
    1 * fileLinesContext.setIntValue(CoreMetrics.NCLOC_DATA_KEY, 3, 0)
    1 * fileLinesContext.setIntValue(CoreMetrics.NCLOC_DATA_KEY, 4, 0)
    1 * fileLinesContext.setIntValue(CoreMetrics.NCLOC_DATA_KEY, 5, 1)
    1 * fileLinesContext.setIntValue(CoreMetrics.COMMENT_LINES_DATA_KEY, 1, 1)
    1 * fileLinesContext.setIntValue(CoreMetrics.COMMENT_LINES_DATA_KEY, 2, 1)
    1 * fileLinesContext.setIntValue(CoreMetrics.COMMENT_LINES_DATA_KEY, 3, 1)
    1 * fileLinesContext.setIntValue(CoreMetrics.COMMENT_LINES_DATA_KEY, 4, 0)
    1 * fileLinesContext.setIntValue(CoreMetrics.COMMENT_LINES_DATA_KEY, 5, 0)
    1 * fileLinesContext.save()
  }
}