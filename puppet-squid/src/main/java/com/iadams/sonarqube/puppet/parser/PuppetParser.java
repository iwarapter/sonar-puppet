package com.iadams.sonarqube.puppet.parser;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.impl.Parser;
import com.iadams.sonarqube.puppet.PuppetConfiguration;
import com.iadams.sonarqube.puppet.api.PuppetGrammar;
import com.iadams.sonarqube.puppet.lexer.PuppetLexer;

/**
 * Created by iwarapter
 */
public class PuppetParser {

    private PuppetParser() {
    }

    public static Parser<Grammar> create(PuppetConfiguration conf) {
        return Parser.builder(PuppetGrammar.create().build())
                .withLexer(PuppetLexer.create(conf)).build();
    }
}