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
package com.iadams.sonarqube.puppet.parser.expressions

import com.iadams.sonarqube.puppet.parser.GrammarSpec
import spock.lang.Unroll

import static com.iadams.sonarqube.puppet.api.PuppetGrammar.ADDITIVE_EXPRESSION
import static com.iadams.sonarqube.puppet.api.PuppetGrammar.ASSIGNMENT_EXPRESSION
import static com.iadams.sonarqube.puppet.api.PuppetGrammar.BOOL_EXPRESSION
import static com.iadams.sonarqube.puppet.api.PuppetGrammar.EXPORTED_RESOURCE_COLLECTOR
import static com.iadams.sonarqube.puppet.api.PuppetGrammar.EXPRESSION
import static com.iadams.sonarqube.puppet.api.PuppetGrammar.MATCH_EXPRESSION
import static com.iadams.sonarqube.puppet.api.PuppetGrammar.MULTIPLICATIVE_EXPRESSION
import static com.iadams.sonarqube.puppet.api.PuppetGrammar.SHIFT_EXPRESSION
import static com.iadams.sonarqube.puppet.api.PuppetGrammar.UNARY_NOT_EXPRESSION
import static org.sonar.sslr.tests.Assertions.assertThat

/**
 * Created by iwarapter
 */
class ExpressionSpec extends GrammarSpec {

	@Unroll
	def "\"#input\" parse correctly"() {
		given:
		setRootRule(EXPRESSION)

		expect:
		assertThat(p).matches(input)

		where:
		input << ['true == true',
				'5 < 9',
				'($operatingsystem != \'Solaris\')',
				'$var = 10',
				'!str2bool($is_virtual)']
	}

	def "Assignment parse correctly"() {
		given:
		setRootRule(ASSIGNMENT_EXPRESSION)

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
	}

	def "unary (not) expressions parse"(){
		given:
		setRootRule(UNARY_NOT_EXPRESSION)

		expect:
		assertThat(p).matches('!$var')
		assertThat(p).matches('!(true)')
	}

	def "boolean expressions parse correctly"(){
		given:
		setRootRule(BOOL_EXPRESSION)

		expect:
		assertThat(p).matches(input)

		where:
		input << ['$purge_configs and $mod_enable_dir',
				  '$purge_configs and !$mod_enable_dir']
	}

	def "mult expressions parse"(){
		given:
		setRootRule(MULTIPLICATIVE_EXPRESSION)

		expect:
		assertThat(p).matches('1 * 1')
		assertThat(p).matches('(1 + 2) * 3')
		assertThat(p).matches('(1 + 2) * (3 * 6)')
	}

	def "addition expressions parse"(){
		given:
		setRootRule(ADDITIVE_EXPRESSION)

		expect:
		assertThat(p).matches('1 + 1')
		assertThat(p).matches('(1 + 2) + 3')
		assertThat(p).matches('(1 + 2) - (3 * 6)')
	}

	def "shift expressions parse"(){
		given:
		setRootRule(SHIFT_EXPRESSION)

		expect:
		assertThat(p).matches('1 << 1')
		assertThat(p).matches('(1 + 2) << 3')
		assertThat(p).matches('(1 + 2) >> (3 * 6)')
	}

	def "regex match expression parse"() {
		given:
		setRootRule(MATCH_EXPRESSION)

		expect:
		assertThat(p).matches('$mod_path =~ /\\//')
	}

	@Unroll
	def "in expression is supported: #snippet"(){
		given:
		setRootRule(EXPRESSION)

		expect:
		assertThat(p).matches(snippet)

		where:
		snippet	<< ["'eat' in 'eaten'",
				   "'Eat' in 'eaten'",
				   "'eat' in ['eat', 'ate', 'eating']",
				   "'Eat' in ['eat', 'ate', 'eating']",
				   "'eat' in { 'eat' => 'present tense', 'ate' => 'past tense'}",
				   "'eat' in { 'present' => 'eat', 'past' => 'ate' }",
				   "/(?i:EAT)/ in ['eat', 'ate', 'eating']",
				   "Integer[100, 199] in [1, 2, 125]",
				   "Integer[100, 199] in [1, 2, 25]"]
	}
}