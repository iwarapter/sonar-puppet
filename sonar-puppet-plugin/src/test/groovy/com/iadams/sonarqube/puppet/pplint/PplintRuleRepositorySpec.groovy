package com.iadams.sonarqube.puppet.pplint

import spock.lang.Specification
import org.sonar.api.rules.Rule
import org.sonar.api.rules.XMLRuleParser

/**
 * Created by iwarapter
 */
class PplintRuleRepositorySpec extends Specification {

    def "CreateRules"() {
        given:
        PplintRuleRepository rulerep = new PplintRuleRepository(new XMLRuleParser())
        List<Rule> rules = rulerep.createRules()

        expect:
        rules.size() == 32
    }
}
