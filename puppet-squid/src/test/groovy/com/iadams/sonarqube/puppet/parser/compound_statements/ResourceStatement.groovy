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

import static com.iadams.sonarqube.puppet.api.PuppetGrammar.RESOURCE
import static org.sonar.sslr.tests.Assertions.assertThat

/**
 * Created by iwarapter
 */
public class ResourceStatement extends GrammarSpec {

	def setup(){
		setRootRule(RESOURCE)
	}

	def "resource with class notify parses correctly"() {
		expect:
		assertThat(p).matches('''package { 'httpd':
								 	ensure => $package_ensure,
								 	name   => $apache_name,
								 	notify => Class['Apache::Service'],
								 }''')
	}

	def "resource with package require parses correctly"() {
		expect:
		assertThat(p).matches('''user { $user:
									ensure  => present,
									gid     => $group,
									require => Package['httpd'],
								 }''')
	}

	def "support exec resources"(){
		expect:
		assertThat(p).matches('''Exec {
								 	path => '/bin:/sbin:/usr/bin:/usr/sbin',
								 }''')
	}

	def "check fully qualified type"(){
		expect:
		assertThat(p).matches('''concat::fragment { 'Apache ports header':
								ensure  => present,
								target  => $ports_file,
								content => template('apache/ports_header.erb')
							  }''')
	}

	def "array of titles"(){
		expect:
		assertThat(p).matches('''file { ['/etc',
										'/etc/rc.d',
										'/etc/rc.d/init.d',
										'/etc/rc.d/rc0.d',
										'/etc/rc.d/rc1.d',
										'/etc/rc.d/rc2.d',
										'/etc/rc.d/rc3.d',
										'/etc/rc.d/rc4.d',
										'/etc/rc.d/rc5.d',
										'/etc/rc.d/rc6.d']:
								  ensure => directory,
								  owner  => 'root',
								  group  => 'root',
								  mode   => '0755',
								}''')
	}
}