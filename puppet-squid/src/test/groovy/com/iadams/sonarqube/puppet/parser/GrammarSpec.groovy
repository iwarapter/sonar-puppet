package com.iadams.sonarqube.puppet.parser

import com.google.common.base.Charsets
import com.iadams.sonarqube.puppet.PuppetConfiguration;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.impl.Parser;
import org.sonar.sslr.grammar.GrammarRuleKey
import spock.lang.Specification;

/**
 * Created by iwarapter
 */
public class GrammarSpec extends Specification {
    protected Parser<Grammar> p = PuppetParser.create(new PuppetConfiguration(Charsets.UTF_8));

    protected void setRootRule(GrammarRuleKey ruleKey) {
        p.setRootRule(p.getGrammar().rule(ruleKey));
    }
}