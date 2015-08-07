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
package com.iadams.sonarqube.puppet

import com.iadams.sonarqube.functional.FunctionalSpecBase

class PuppetResourcesSpec extends FunctionalSpecBase {

	def setup(){
		copyResources("example_file_resource.pp", "example_file_resource.pp")
		copyResources("example_user_resource.pp", "example_user_resource.pp")
	}

	def "puppet resources are recorded on a file"(){
		when:
		resetDefaultProfile()
		runSonarRunner()

		then:
		analysisFinishedSuccessfully()
		analysisLogDoesNotContainErrorsOrWarnings()
		theFollowingFileMetricsHaveTheFollowingValue('example_file_resource.pp',[puppet_resources:1, lines:3])
		theFollowingFileMetricsHaveTheFollowingValue('example_user_resource.pp',[puppet_resources:1, lines:4])
	}

	def "puppet resources are recorded on a project"(){
		when:
		resetDefaultProfile()
		runSonarRunner()

		then:
		analysisFinishedSuccessfully()
		analysisLogDoesNotContainErrorsOrWarnings()
		theFollowingProjectMetricsHaveTheFollowingValue([puppet_resources:2, lines:7])
	}
}
