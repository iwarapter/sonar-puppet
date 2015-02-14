package com.iadams.sonarqube.puppet.api;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.TokenType;

/**
 * Created by iwarapter on 16/12/14.
 */
public enum PuppetPunctuator implements TokenType {

    DIV("/"),
    MUL("*"),
    LBRACK("["),
    RBRACK("]"),
    LBRACE("{"),
    RBRACE("}"),
    LPAREN("("),
    RPAREN(")"),
    ISEQUAL("=="),
    MATCH("=~"),
    FARROW("=>"),
    EQUALS("="),
    APPENDS("+="),
    PARROW("+>"),
    PLUS("+"),
    GREATEREQUAL(">="),
    RSHIFT(">>"),
    GREATERTHAN(">"),
    LESSEQUAL("<="),
    LLCOLLECT("<<|"),
    OUT_EDGE("<-"),
    OUT_EDGE_SUB("<~"),
    LCOLLECT("<|"),
    LSHIFT("<<"),
    LESSTHAN("<"),
    NOMATCH("!~"),
    NOTEQUAL("!="),
    NOT("!"),
    RRCOLLECT("|>>"),
    RCOLLECT("|>"),
    IN_EDGE("->"),
    IN_EDGE_SUB("~>"),
    MINUS("-"),
    COMMA(","),
    DOT("."),
    COLON(":"),
    AT("@"),
    SEMIC(";"),
    QMARK("?"),
    BACKSLASH("\\"),
    MODULO("%"),
    PIPE("|");

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
