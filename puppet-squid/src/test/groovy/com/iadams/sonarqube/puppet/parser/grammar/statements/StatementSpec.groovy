package com.iadams.sonarqube.puppet.parser.grammar.statements

import com.iadams.sonarqube.puppet.api.PuppetGrammar
import com.iadams.sonarqube.puppet.parser.GrammarSpec
import spock.lang.Unroll

import static org.sonar.sslr.tests.Assertions.assertThat;

/**
 * Created by iwarapter
 */
class StatementSpec extends GrammarSpec {

    def "if statements"() {
        given:
        setRootRule(PuppetGrammar.EXPRESSION)

        expect:
        assertThat(p)
                .matches("==")
                //s.matches("1 - 1")
    }
}