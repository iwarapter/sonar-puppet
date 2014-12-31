package com.iadams.sonarqube.puppet.pplint

import org.sonar.api.rules.Rule
import org.sonar.api.rules.RuleRepository
import org.sonar.api.rules.XMLRuleParser
import com.iadams.sonarqube.puppet.core.Puppet

/**
 * Created by iwarapter
 */
class PplintRuleRepository extends RuleRepository {

    static final String REPOSITORY_NAME = 'Pplint'
    static final String REPOSITORY_KEY = REPOSITORY_NAME

    static final String RULES_FILE = '/com/iadams/sonarqube/puppet/pplint/rules.xml'
    final XMLRuleParser ruleParser

    PplintRuleRepository(XMLRuleParser ruleParser) {
        super( REPOSITORY_KEY, Puppet.KEY )
        setName( REPOSITORY_NAME )
        this.ruleParser = ruleParser
    }

    @Override
    List<Rule> createRules() {
        List<Rule> rules = ruleParser.parse(getClass().getResourceAsStream(RULES_FILE))
        rules.each{ it.setRepositoryKey(REPOSITORY_KEY)}

        return rules
    }
}