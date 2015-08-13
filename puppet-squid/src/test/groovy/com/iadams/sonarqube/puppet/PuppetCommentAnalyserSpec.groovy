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
import spock.lang.Unroll

class PuppetCommentAnalyserSpec extends Specification {

  PuppetCommentAnalyser analyser

  def setup() {
    analyser = new PuppetCommentAnalyser()
  }

  @Unroll
  def "check line with #testFor is blank #output"() {
    expect:
    analyser.isBlank(input) == output

    where:
    input   | output | testFor
    '    '  | true   | 'spaces'
    '   '   | true   | 'tabs'
    'words' | false  | 'letters'
    '12345' | false  | 'numbers'
    '12wor' | false  | 'letters and numbers'
  }

  //https://docs.puppetlabs.com/puppet/latest/reference/lang_comments.html

  @Unroll
  def "get comments"() {
    expect:
    analyser.getContents(input) == output

    where:
    input                      | output
    '# comment'                | ' comment'
    '/* comment */'            | ' comment '
    '/* comment1\ncomment2 */' | ' comment1\ncomment2 '
  }

  def "unknown comment type"() {
    when:
    analyser.getContents('')

    then:
    thrown(IllegalArgumentException)
  }
}
