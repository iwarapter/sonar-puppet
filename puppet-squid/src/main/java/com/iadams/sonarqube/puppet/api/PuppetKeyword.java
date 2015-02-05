package com.iadams.sonarqube.puppet.api;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.TokenType;


/**
 * Created by iwarapter
 */
public enum PuppetKeyword implements TokenType {
    BEFORE("before"),
    CASE("case"),
    CLASS("class"),
    DEFAULT("default"),
    DEFINE("define"),
    ELSE("else"),
    ELSIF("elsif"),
    FALSE("false"),
    IF("if"),
    IMPORT("import"),
    INHERITS("inherits"),
    NODE("node"),
    NOTIFY("notify"),
    REQUIRE("require"),
    SUBSCRIBE("subscribe"),
    TRUE("true"),
    UNDEF("undef"),
    UNLESS("unless");

    private final String value;

    private PuppetKeyword(String value) {
        this.value = value;
    }

    public String getName() {
        return name();
    }

    public String getValue() {
        return value;
    }

    public boolean hasToBeSkippedFromAst(AstNode node) {
        return false;
    }

    public static String[] keywordValues() {
        PuppetKeyword[] keywordsEnum = PuppetKeyword.values();
        String[] keywords = new String[keywordsEnum.length];
        for (int i = 0; i < keywords.length; i++) {
            keywords[i] = keywordsEnum[i].getValue();
        }
        return keywords;
    }
}
