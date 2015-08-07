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
package com.iadams.sonarqube.puppet.pplint

import spock.lang.Specification

class PplintIssuesAnalyzerSpec extends Specification {

    def "should parse correctly"() {
        given:
        String resourceName = "/com/iadams/sonarqube/puppet/pplint/sample_puppet_lint_output.txt"
        String pathName = getClass().getResource(resourceName).getPath();
        String pplintPath = null
        def issueAnalyzer = new PplintIssuesAnalyzer(pplintPath)

        when:
        def lines = new File(pathName).readLines()
        def issues = issueAnalyzer.parseOutput(lines)

        then:
        issues.size() == 4
    }

    def "blank output should return no issues"(){
        expect:
        new PplintIssuesAnalyzer(null).parseOutput([""]).isEmpty()
    }

    def "parse an unknown rule"() {
        given:
        def exampleOutput = ['"/file.pp - a_new_rule 4 WARNING some new rule that has yet to be added"']
        expect:
        new PplintIssuesAnalyzer(null).parseOutput(exampleOutput).ruleId.contains('a_new_rule')
    }
}
