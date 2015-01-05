package com.iadams.sonarqube.puppet.pplint

import spock.lang.Specification

/**
 * Created by iwarapter
 */
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
        new PplintIssuesAnalyzer(null).parseOutput('').isEmpty()
    }

    def "parse an unknown rule"() {
        given:
        def exampleOutput = ['"/file.pp - a_new_rule 4 WARNING some new rule that has yet to be added"']
        expect:
        new PplintIssuesAnalyzer(null).parseOutput(exampleOutput).ruleId.contains('a_new_rule')
    }
}
