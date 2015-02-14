package com.iadams.sonarqube.puppet.api

import com.sonar.sslr.api.AstNode
import spock.lang.Specification

/**
 * Created by iwarapter on 16/12/14.
 */
class PuppetPunctuatorSpec extends Specification {

    def "test"() {
        given:
        AstNode astNode = Mock(AstNode)

        expect:
        PuppetPunctuator.values().size() == 42

        PuppetPunctuator.values().each{
            it.hasToBeSkippedFromAst(astNode) == false
        }
    }
}
