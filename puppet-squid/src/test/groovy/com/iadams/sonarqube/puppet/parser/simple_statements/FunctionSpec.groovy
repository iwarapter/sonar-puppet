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

import static com.iadams.sonarqube.puppet.api.PuppetGrammar.FUNCTION_STMT
import static org.sonar.sslr.tests.Assertions.assertThat

class FunctionSpec extends GrammarSpec {

    def setup() {
        setRootRule(FUNCTION_STMT)
    }

    def "simple function call parses"() {
        expect:
        assertThat(p).matches('merge($_directory, $_directory_version)')
        assertThat(p).matches('merge($_directory, $_directory_version,)')
        assertThat(p).matches("fail 'hello'")
        assertThat(p).matches('notice ($foo[(1 + 2)])')
    }

    def "simple contain statement parse"() {
        expect:
        assertThat(p).matches('contain apache')
        assertThat(p).matches("contain Class['apache']")
        assertThat(p).matches('contain ntp::service')
    }

    def "complex contain statements parse"(){
        expect:
        assertThat(p).matches('contain [abc, def]')
        assertThat(p).matches('contain abc, def')
    }


    def "simple require statement parse"() {
        expect:
        assertThat(p).matches('require apache')
        assertThat(p).matches("require Class['apache']")
    }

    def "complex require statements parse"(){
        expect:
        assertThat(p).matches('require [abc, def]')
        assertThat(p).matches('require abc, def')
    }

    def "simple include parses correctly"() {
        expect:
        assertThat(p).matches('include common')
        assertThat(p).matches("include 'common'")
        assertThat(p).matches('include role::solaris')
    }

    def 'include a class reference'(){
        expect:
        assertThat(p).matches("include Class['base::linux']")
    }

    def 'include a list'(){
        expect:
        assertThat(p).matches('include common, apache')
    }

    def 'including variable (for an array)'(){
        expect:
        assertThat(p).matches('include $my_classes')
    }

	def "function using hash array accessor"(){
		expect:
		assertThat(p).matches('dirname($logfiles[1][1])')
	}
}