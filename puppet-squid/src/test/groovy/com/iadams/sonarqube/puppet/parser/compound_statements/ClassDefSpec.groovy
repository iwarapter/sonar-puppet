/*
 * Sonar Puppet Plugin
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
package com.iadams.sonarqube.puppet.parser.compound_statements

import com.iadams.sonarqube.puppet.parser.GrammarSpec

import static com.iadams.sonarqube.puppet.api.PuppetGrammar.CLASSDEF
import static org.sonar.sslr.tests.Assertions.assertThat

/**
 * Created by iwarapter
 */
class ClassDefSpec extends GrammarSpec {

    def setup() {
        setRootRule(CLASSDEF)
    }

	def "simple class parses correctly"() {
		expect:
		assertThat(p).matches("""# A class with no parameters
            class apache {
              file { '/etc/passwd':
                owner => 'root',
                group => 'root',
                mode  => '0644',
              }
            }
        """)
	}

    def "class with inherits parses correctly"() {
        expect:
        assertThat(p).matches('class ssh inherits server { }')
    }

    def "scoped classname with inherits parses correctly"(){
        expect:
        assertThat(p).matches('class ssh::client inherits workstation { }')
    }

    def "classes with complex parameters"(){
        expect:
        assertThat(p).matches('''class apache(
                                    $apache_name            = $::apache::params::apache_name,
                                    $default_mods           = true,
                                    $default_charset        = undef,
                                    $default_ssl_vhost      = false,
                                    $default_type           = 'none',
                                    $log_formats            = {},
                                 ) inherits ::apache::params { }''')
    }
}