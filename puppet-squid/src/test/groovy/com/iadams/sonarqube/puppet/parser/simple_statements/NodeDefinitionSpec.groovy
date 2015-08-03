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

import static com.iadams.sonarqube.puppet.api.PuppetGrammar.NODE_DEFINITION
import static org.sonar.sslr.tests.Assertions.assertThat

class NodeDefinitionSpec extends GrammarSpec {

	def setup(){
		setRootRule(NODE_DEFINITION)
	}

	def "simple node parses correctly"() {
		expect:
		assertThat(p).matches("""node 'server1' {
									include common
								}""")
	}

	def "nodes can define inheritance"() {
		expect:
		assertThat(p).matches("""node 'server1' inherits 'server2' {
									include common
								}""")
	}

	def "multi node definition parses correctly"() {
		expect:
		assertThat(p).matches("""node 'server1', 'server2', 'server3' {
									include common
								}""")

		assertThat(p).matches("""node 'www1.example.com', 'www2.example.com', 'www3.example.com' {
								  include common
								  include apache, squid
								}""")
	}
}
