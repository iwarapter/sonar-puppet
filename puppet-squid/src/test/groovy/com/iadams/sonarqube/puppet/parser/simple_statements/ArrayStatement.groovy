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
package com.iadams.sonarqube.puppet.parser.simple_statements

import com.iadams.sonarqube.puppet.parser.GrammarSpec

import static com.iadams.sonarqube.puppet.api.PuppetGrammar.ARRAY
import static org.sonar.sslr.tests.Assertions.assertThat

/**
 * Created by iwarapter
 */
class ArrayStatement extends GrammarSpec {

	def setup(){
		setRootRule(ARRAY)
	}

	def "empty array parses"(){
		expect:
		assertThat(p).matches('[]')
	}

	def "example array parses correctly"() {
		expect:
		assertThat(p).matches("[ 'one', 'two', 'three' ]")
		assertThat(p).matches("[ 'one', 'two', 'three', ]")
	}

	def "arrays with hash parse correctly"(){
		expect:
		assertThat(p).matches("[ 'one', {'second' => 'two', 'third' => 'three'} ]")
	}

	def "array from function call parses"(){
		expect:
		assertThat(p).matches('[ merge($_directory, $_directory_version) ]')
	}

	def "nested array parses"(){
		expect:
		assertThat(p).matches("[['one', 'two'], ['three', 'four']]")
	}

	def "complex nested array parses"(){
		expect:
		assertThat(p).matches('''[$sonarqube_verify_phase ? {
									  false   => [
										{
										  goals       => $mvn_goal_sonarqube,
										  mvn_version => $mvn_version_sonarqube,
										}
									  ],
									  default => [],
									},
									[{
									  goals       => $mvn_goal_push_to_nexus,
									  mvn_version => $mvn_version,
									}]
								  ]''')
	}
}