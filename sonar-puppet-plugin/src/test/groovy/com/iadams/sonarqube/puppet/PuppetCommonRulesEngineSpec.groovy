package com.iadams.sonarqube.puppet

import org.sonar.commonrules.api.CommonRulesRepository
import spock.lang.Specification


/**
 * Created by iwarapter
 */
class PuppetCommonRulesEngineSpec extends Specification {

    def "should provide expected extensions"() {
        given:
        PuppetCommonRulesEngine engine = new PuppetCommonRulesEngine()

        expect:
        !engine.provide().isEmpty()
    }

    def "enable common rules"() {
        given:
        PuppetCommonRulesEngine engine = new PuppetCommonRulesEngine()
        CommonRulesRepository repo = engine.newRepository()

        expect:
        repo.rules().size() == 4
        repo.rule(CommonRulesRepository.RULE_INSUFFICIENT_COMMENT_DENSITY).isEnabled()
    }
}
