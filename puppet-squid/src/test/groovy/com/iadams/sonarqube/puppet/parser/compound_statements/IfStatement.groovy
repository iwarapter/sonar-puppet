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
package com.iadams.sonarqube.puppet.parser.compound_statements

import com.iadams.sonarqube.puppet.parser.GrammarSpec

import static com.iadams.sonarqube.puppet.api.PuppetGrammar.IF_STMT
import static org.sonar.sslr.tests.Assertions.assertThat

public class IfStatement extends GrammarSpec {

  def setup() {
    setRootRule(IF_STMT)
  }

  def "simple if statement parses correctly"() {
    expect:
    assertThat(p).matches('if 1 == 1 {}')
  }

  def "if statement with else parses correctly"() {
    expect:
    assertThat(p).matches('''if str2bool("$is_virtual") {
			  # Our NTP module is not supported on virtual machines:
			  warning( 'Tried to include class ntp on virtual machine.' )
			}
			else {
			  # Normal node, include the class.
			  include ntp
			}''')
  }

  def "if statement with elseif parses correctly"() {
    expect:
    assertThat(p).matches('''if str2bool("$is_virtual") {
			  # Our NTP module is not supported on virtual machines:
			  warning( 'Tried to include class ntp on virtual machine.' )
			}
			elsif $operatingsystem == 'Darwin' {
			  warning( 'This NTP module does not yet work on our Mac laptops.' )
			}''')
  }

  def "if statement with variable condition parses correctly"() {
    expect:
    assertThat(p).matches('''if $mpm_module {
									validate_re($mpm_module, $valid_mpms_re)
								 }''')
  }

  def "compact if statement parses"() {
    expect:
    assertThat(p).matches('''if $purge_vhost_dir == undef {
									$purge_vhostd = $purge_confd
								  } else {
									$purge_vhostd = $purge_vhost_dir
								  }''')
  }

  def "if elseif else statement"() {
    expect:
    assertThat(p).matches('''if $lib {
									$_lib = $lib
								  } elsif has_key($mod_libs, $mod) { # 2.6 compatibility hack
									$_lib = $mod_libs[$mod]
								  } else {
									$_lib = "mod_${mod}.so"
								  }''')
  }

  def "if with condition in parenthesis"() {
    expect:
    assertThat(p).matches('if ($::operatingsystem == \'Amazon\') {}')
  }

  def "if without parenthesis"(){
    expect:
    assertThat(p).matches('if $ensure == present {}')
  }
}
