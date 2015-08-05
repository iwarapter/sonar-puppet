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
package com.iadams.sonarqube.puppet.parser.expressions

import com.iadams.sonarqube.puppet.parser.GrammarSpec
import spock.lang.Unroll

import static com.iadams.sonarqube.puppet.api.PuppetGrammar.ADDITIVE_EXPRESSION
import static com.iadams.sonarqube.puppet.api.PuppetGrammar.ASSIGNMENT_EXPRESSION
import static com.iadams.sonarqube.puppet.api.PuppetGrammar.BOOL_EXPRESSION
import static com.iadams.sonarqube.puppet.api.PuppetGrammar.EXPRESSION
import static com.iadams.sonarqube.puppet.api.PuppetGrammar.EXPRESSIONS
import static com.iadams.sonarqube.puppet.api.PuppetGrammar.MATCH_EXPRESSION
import static com.iadams.sonarqube.puppet.api.PuppetGrammar.MULTIPLICATIVE_EXPRESSION
import static com.iadams.sonarqube.puppet.api.PuppetGrammar.SHIFT_EXPRESSION
import static com.iadams.sonarqube.puppet.api.PuppetGrammar.UNARY_NOT_EXPRESSION
import static org.sonar.sslr.tests.Assertions.assertThat

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
				//'$kernel in [\'linux\', \'solaris\']',
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
	def "expressions parse correctly"() {
		given:
		setRootRule(EXPRESSIONS)

		expect:
		assertThat(p).matches(input)

		where:
		input << ['1 == 1',
				  'func($var)',
				  '$var1 and ! defined(File[$var2])',
				  'versioncmp($apache_version, \'2.4\') < 0',
				  '$::operatingsystem == \'Amazon\'']
	}

	def "2 expressions parses"() {
		given:
		setRootRule(EXPRESSIONS)

		expect:
		assertThat(p).matches('$::operatingsystem == \'Ubuntu\' and $::lsbdistrelease == \'10.04\'')
	}

	def "complex expressions parse"(){
		given:
		setRootRule(EXPRESSIONS)

		expect:
		assertThat(p).matches('($scriptalias or $scriptaliases != []) or ($redirect_source and $redirect_dest)')
	}
}
