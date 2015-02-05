package com.iadams.sonarqube.puppet.api;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.TokenType;

/**
 * Created by iwarapter on 16/12/14.
 */
public enum PuppetPunctuator implements TokenType {

    //Comparison Operators
    EQU("=="),
    NOT_EQU("!="),
    LT("<"),
    GT(">"),
    LT_EQU("<="),
    GT_EQU(">="),
    REG_MAT("=~"),
    REG_NMAT("!~"),
    IN("in"),

    //Boolean Operators
    AND("and"),
    OR("or"),
    NOT("!"),

    //Arithmetic Operators
    PLUS("+"),
    MINUS("-"),
    DIV("/"),
    MUL("*"),
    MOD("%"),
    L_SHIFT("<<"),
    R_SHIFT(">>"),


    // Delimiters
    BACKTICK("`"),
    LPARENTHESIS("("),
    RPARENTHESIS(")"),
    LBRACKET("["),
    RBRACKET("]"),
    LCURLYBRACE("{"),
    RCURLYBRACE("}"),
    COMMA(","),
    COLON(":"),
    DOT("."),
    SEMICOLON(";"),
    AT("@"),
    ASSIGN("="),
    PLUS_ASSIGN("+="),
    MINUS_ASSIGN("-="),
    MUL_ASSIGN("*="),
    DIV_ASSIGN("/="),
    DIV_DIV_ASSIGN("//="),
    MOD_ASSIGN("%="),
    AND_ASSIGN("&="),
    OR_ASSIGN("|="),
    XOR_ASSIGN("^="),
    RIGHT_ASSIGN(">>="),
    LEFT_ASSIGN("<<="),
    MUL_MUL_ASSIGN("**=");

    private final String value;

    private PuppetPunctuator(String word) {
        this.value = word;
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
}
