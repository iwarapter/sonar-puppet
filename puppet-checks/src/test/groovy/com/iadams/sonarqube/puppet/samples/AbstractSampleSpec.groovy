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
package com.iadams.sonarqube.puppet.samples

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class AbstractSampleSpec extends Specification {

  @Rule
  TemporaryFolder testProjectDir

  static boolean isPuppetAvailable() {
    try {
      def p = "puppet --version".execute()
      def output = p.text
      println "Detected Puppet version: $output"
      return output.contains('3.8.') && p.exitValue() == 0
    }
    catch (IOException e) {
      return false
    }
  }

  void validateSample(def sample) {

    def sampleFile = testProjectDir.newFile()
    sampleFile.write(sample)

    def p = "puppet parser validate ${sampleFile.absolutePath}".execute()
    p.waitFor()
    p.waitFor()
    assert p.exitValue() == 0: """
        *****
        Failed to execute sample:
        -Sample:
        ${sampleFile.text}
        -Problem: ${p.err.text}
        *****
        """.stripIndent()
  }

  def findSamples(String filePath) {
    File file = new File(filePath)
    def regex = /(?ms).*?<pre>(.*?)<\\/pre>(.*?)/

    def samples = []
    file.text.eachMatch(regex) {
      samples.add(it[1])
    }
    samples
  }

}
