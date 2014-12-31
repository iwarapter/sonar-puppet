package com.iadams.sonarqube.puppet.api

import spock.lang.Specification
import com.sonar.sslr.api.AstNode

/**
 * Created by iwarapter on 16/12/14.
 */
class PuppetTokenTypeSpec extends Specification {
    def "test all token exist"(){
        expect:
        AstNode astNode = Mock(AstNode)
        for(PuppetTokenType tokenType : PuppetTokenType.values()){
            tokenType.getName() == tokenType.name
            tokenType.getValue() == tokenType.name
            tokenType.hasToBeSkippedFromAst(astNode) == false
        }
    }
}