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
package com.iadams.sonarqube.puppet.parser.simple_statements

import com.iadams.sonarqube.puppet.parser.GrammarSpec

import static com.iadams.sonarqube.puppet.api.PuppetGrammar.RELATIONSHIP
import static org.sonar.sslr.tests.Assertions.assertThat

class RelationshipSpec extends GrammarSpec {

  def setup() {
    setRootRule(RELATIONSHIP)
  }

  def "left right relationship chaining passes correctly"() {
    expect:
    assertThat(p).matches("File['/etc/ntp.conf'] ~> Service['ntpd']")
    assertThat(p).matches("File['/etc/ntp.conf'] -> Service['ntpd']")
  }

  def "right left relationship chaining passes correctly"() {
    expect:
    assertThat(p).matches("File['/etc/ntp.conf'] <~ Service['ntpd']")
    assertThat(p).matches("File['/etc/ntp.conf'] <- Service['ntpd']")
  }

  def "nested relationship chains"() {
    expect:
    assertThat(p).matches("Package['ntp'] -> File['/etc/ntp.conf'] ~> Service['ntpd']")
    assertThat(p).matches("Package['ntp'] <- File['/etc/ntp.conf'] <~ Service['ntpd']")
  }

  def "relationships with collector works"() {
    expect:
    assertThat(p).matches('Package[$_package] -> File<| title == "${mod}.conf" |>')
    assertThat(p).matches('Postgresql::Server::Database<|title == $database_name|> -> Exec[$exec_name]')
  }

  def "relationship from resource declarations work"() {
    expect:
    assertThat(p).matches("anchor { 'postgresql::server::plpython::start': }-> Class['postgresql::server::install']")
  }

  def "complex nested relationships parse"() {
    expect:
    assertThat(p).matches("""anchor { 'postgresql::server::postgis::start': }->
								 Class['postgresql::server::install']->
								 Package['postgresql-postgis']->
								 Class['postgresql::server::service']->
								 anchor { 'postgresql::server::postgis::end': }""")
  }

  def "class resource ref can be used in relationship"() {
    expect:
    assertThat(p).matches('class { "${pg}::passwd": }->  anchor { "${pg}::end": }')
  }
}