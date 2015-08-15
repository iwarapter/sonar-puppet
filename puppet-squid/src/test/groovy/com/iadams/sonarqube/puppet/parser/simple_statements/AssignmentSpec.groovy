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

import static com.iadams.sonarqube.puppet.api.PuppetGrammar.ASSIGNMENT
import static org.sonar.sslr.tests.Assertions.assertThat

class AssignmentSpec extends GrammarSpec {


  def "Assignment parse correctly"() {
    given:
    setRootRule(ASSIGNMENT)

    expect:
    assertThat(p).matches('$var = 10')
    assertThat(p).matches('$var = undef')
    assertThat(p).matches('$var = "double quoted string"')
    assertThat(p).matches('$purge_mod_dir = $purge_configs and !$mod_enable_dir')
    assertThat(p).matches('''$valid_mpms_re = $apache_version ? {
									'2.4'   => '(event|itk|peruser|prefork|worker)',
									default => '(event|itk|prefork|worker)\'
								  }''')
    assertThat(p).matches('$notifies = Class[\'Apache::Service\']')
    assertThat(p).matches('''$_directory_version = {
									require => 'all granted',
								 }''')
    assertThat(p).matches('$director_node = wms3::director_node')
    assertThat(p).matches('$a = String')

    assertThat(p).matches('$var[1] = String')
  }
}