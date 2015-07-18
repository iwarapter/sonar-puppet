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
import spock.lang.Ignore

import static com.iadams.sonarqube.puppet.api.PuppetGrammar.SELECTOR_STMT
import static com.iadams.sonarqube.puppet.api.PuppetGrammar.expressions
import static org.sonar.sslr.tests.Assertions.assertThat

/**
 * @author iwarapter
 */
public class SelectorStatement extends GrammarSpec {

	def setup(){
		setRootRule(SELECTOR_STMT)
	}

	def "selector statement parses correctly"() {
		expect:
		assertThat(p).matches('''$osfamily ? {
			'Solaris'          => 'wheel',
			/(Darwin|FreeBSD)/ => 'wheel',
			default            => 'root',
		}''')
	}

	def "another selector example"(){
		expect:
		assertThat(p).matches('''$::osfamily ? {
								  'freebsd' => true,
								  default   => false
								}
							''')
	}

	def "selector case testing"(){
		expect:
		assertThat(p).matches('''$default_ssl_vhost ? {
								  true  => 'present',
								  false => 'absent\'
								}''')
	}

	def "selector with an undef assignment"(){
		expect:
		assertThat(p).matches('''$::apache::apache_version ? {
						   		   '2.4'   => 'mod_pagespeed_ap24.so',
								   default => undef
								}''')
	}

	def "nested selector statements"(){
		expect:
		assertThat(p).matches('''$source ? {
									undef   => $content ? {
									  undef   => template($template),
									  default => $content,
									},
									default => undef,
								  }''')
	}

	def "selector with arrays"(){
		expect:
		assertThat(p).matches("""\$::apache::params::distrelease ? {
									'6'     => ['/usr/lib/libxml2.so.2'],
									'10'    => ['/usr/lib/libxml2.so.2'],
									default => ["/usr/lib/\${gnu_path}-linux-gnu/libxml2.so.2"],
								  }
								""")
	}

	def "selector with default resource ref"(){
		expect:
		assertThat(p).matches("""\$::osfamily ? {
						  'freebsd' => [
							File[\$_loadfile_name],
							File["\${::apache::conf_dir}/\${::apache::params::conf_file}"]
						  ],
						  default => File[\$_loadfile_name],
						}""")
	}

	def "selected with default selector using func call as the control var"(){
		expect:
		assertThat(p).matches('''$::operatingsystemrelease ? {
								  /5/     => 'postgis',
								  default => versioncmp($postgis_version, '2') ? {
									'-1'    => "postgis${package_version}",
									default => "postgis2_${package_version}",}
								}''')
	}

	def "selector with default only should parse"(){
		expect:
		assertThat(p).matches('''$env_type ? {
								  default => '0755\'
								}''')
	}

	def "should handle integers in a selector case"(){
		expect:
		assertThat(p).matches('''$titi ? {
								  24 => 'abc',
								}''')
	}
}
