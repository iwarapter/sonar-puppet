package com.iadams.sonarqube.puppet.pplint;

import com.iadams.sonarqube.puppet.Puppet;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleRepository;
import org.sonar.api.rules.XMLRuleParser;

import java.util.List;

/**
 * Created by iwarapter
 */
public class PplintRuleRepository extends RuleRepository {

    public static final String REPOSITORY_NAME = "Pplint";
    public static final String REPOSITORY_KEY = REPOSITORY_NAME;

    private static final String RULES_FILE = "/com/iadams/sonarqube/puppet/pplint/rules.xml";
    private final XMLRuleParser ruleParser;

    public PplintRuleRepository(XMLRuleParser ruleParser) {
        super(REPOSITORY_KEY, Puppet.KEY);
        setName(REPOSITORY_NAME);
        this.ruleParser = ruleParser;
    }

    @Override
    public List<Rule> createRules() {
        List<Rule> rules = ruleParser.parse(getClass().getResourceAsStream(RULES_FILE));
        for (Rule r : rules) {
            r.setRepositoryKey(REPOSITORY_KEY);
        }
        return rules;
    }
}
