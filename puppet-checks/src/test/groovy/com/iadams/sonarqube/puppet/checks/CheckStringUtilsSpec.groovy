/*
 * SonarQube Puppet Plugin
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Iain Adams
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

import spock.lang.Specification

class CheckStringUtilsSpec extends Specification {

  def "strings should contain variables"() {
    given:
    boolean b1 = CheckStringUtils.containsVariable("\$abc")
    boolean b2 = CheckStringUtils.containsVariable("\${abc}")
    boolean b3 = CheckStringUtils.containsVariable("\$abc blabla")
    boolean b4 = CheckStringUtils.containsVariable("\${abc} blabla")
    boolean b5 = CheckStringUtils.containsVariable("blabla\$abc blabla")
    boolean b6 = CheckStringUtils.containsVariable("blabla\${abc} blabla")

    expect:
    assert b1 == true
    assert b2 == true
    assert b3 == true
    assert b4 == true
    assert b5 == true
    assert b6 == true
  }

  def "strings should not contain variables"() {
    given:
    boolean b1 = CheckStringUtils.containsVariable("\\\$abc")
    boolean b2 = CheckStringUtils.containsVariable("\\\${abc}")
    boolean b3 = CheckStringUtils.containsVariable("\\\$abc blabla")
    boolean b4 = CheckStringUtils.containsVariable("\\\${abc} blabla")
    boolean b5 = CheckStringUtils.containsVariable("blabla\\\$abc blabla")
    boolean b6 = CheckStringUtils.containsVariable("blabla\\\${abc} blabla")
    boolean b7 = CheckStringUtils.containsVariable("abc")
    boolean b8 = CheckStringUtils.containsVariable("\$ abc")
    boolean b9 = CheckStringUtils.containsVariable("\${ abc")
    boolean b10 = CheckStringUtils.containsVariable("\${abc")

    expect:
    assert b1 == false
    assert b2 == false
    assert b3 == false
    assert b4 == false
    assert b5 == false
    assert b6 == false
    assert b7 == false
    assert b8 == false
    assert b9 == false
    assert b10 == false
  }

  def "strings should contain special characters"() {
    given:
    boolean b1 = CheckStringUtils.containsSpecialCharacter("\"")
    boolean b2 = CheckStringUtils.containsSpecialCharacter("'")
    boolean b3 = CheckStringUtils.containsSpecialCharacter("\\n")
    boolean b4 = CheckStringUtils.containsSpecialCharacter("\\r")
    boolean b5 = CheckStringUtils.containsSpecialCharacter("\\t")

    expect:
    assert b1 == true
    assert b2 == true
    assert b3 == true
    assert b4 == true
    assert b5 == true
  }

  def "strings should not contain special characters"() {
    given:
    boolean b1 = CheckStringUtils.containsSpecialCharacter("abc")
    boolean b2 = CheckStringUtils.containsSpecialCharacter("n")

    expect:
    assert b1 == false
    assert b2 == false
  }

  def "strings should contain variable only"() {
    given:
    boolean b1 = CheckStringUtils.containsOnlyVariable("\$abc")
    boolean b2 = CheckStringUtils.containsOnlyVariable("\$::abc")
    boolean b3 = CheckStringUtils.containsOnlyVariable("\$abc::def")
    boolean b4 = CheckStringUtils.containsOnlyVariable("\${abc}")
    boolean b5 = CheckStringUtils.containsOnlyVariable("\${::abc}")
    boolean b6 = CheckStringUtils.containsOnlyVariable("\${abc::def}")

    expect:
    assert b1 == true
    assert b2 == true
    assert b3 == true
    assert b4 == true
    assert b5 == true
    assert b6 == true
  }

  def "strings should not contain variable only"() {
    given:
    boolean b1 = CheckStringUtils.containsOnlyVariable("\$abc ")
    boolean b2 = CheckStringUtils.containsOnlyVariable("\$::abc ")
    boolean b3 = CheckStringUtils.containsOnlyVariable("\$abc::def ")
    boolean b4 = CheckStringUtils.containsOnlyVariable("\${abc} ")
    boolean b5 = CheckStringUtils.containsOnlyVariable("\${::abc} ")
    boolean b6 = CheckStringUtils.containsOnlyVariable("\${abc::def} ")
    boolean b7 = CheckStringUtils.containsOnlyVariable("\${abc:def} ")

    expect:
    assert b1 == false
    assert b2 == false
    assert b3 == false
    assert b4 == false
    assert b5 == false
    assert b6 == false
    assert b7 == false
  }

}
