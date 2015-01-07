package com.iadams.sonarqube.puppet.api;

import com.sonar.sslr.api.AstNode;

/**
 * Created by iwarapter on 16/12/14.
 */
public enum PuppetTokenType {
    NUMBER,
    STRING,

    INDENT,
    DEDENT,
    NEWLINE;

    public String getName() {
        return name();
    }

    public String getValue() {
        return name();
    }

    public boolean hasToBeSkippedFromAst(AstNode node) {
        return false;
    }

}
