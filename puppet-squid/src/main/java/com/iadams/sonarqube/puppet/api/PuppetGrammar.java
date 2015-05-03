package com.iadams.sonarqube.puppet.api;

import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.grammar.LexerfulGrammarBuilder;

import static com.iadams.sonarqube.puppet.api.PuppetKeyword.*;
import static com.iadams.sonarqube.puppet.api.PuppetPunctuator.*;
import static com.iadams.sonarqube.puppet.api.PuppetTokenType.NEWLINE;
import static com.sonar.sslr.api.GenericTokenType.LITERAL;
import static com.sonar.sslr.api.GenericTokenType.EOF;

/**
 * Created by iwarapter
 */
public enum PuppetGrammar  implements GrammarRuleKey {

    CONDITION,

    //EXPRESSIONS
    EXPRESSION,
    ASSIGNMENT_EXPRESSION,
    COMP_OPERATOR,
    A_OPER,
    M_OPER,
    MATCH_OPERATOR,

    SHIFT_OPER,

    //SIMPLE STATEMENTS
    STATEMENT,
    RESOURCE,
    VIRTUALRESOURCE,
    COLLECTION,

    //CONDITIONAL STATEMENTS
    CONDITION_CLAUSE,
    IF_STATEMENT,
    UNLESS_STMT,

    FILE_INPUT;

    public static LexerfulGrammarBuilder create() {
        LexerfulGrammarBuilder b = LexerfulGrammarBuilder.create();

        b.rule(FILE_INPUT).is(b.zeroOrMore(b.firstOf(NEWLINE, STATEMENT)), EOF);
        b.rule(STATEMENT).is(b.oneOrMore(EXPRESSION));

        grammar(b);
        conditionalStatements(b);
        //simpleStatements(b);
        expressions(b);

        b.setRootRule(FILE_INPUT);
        b.buildWithMemoizationOfMatchesForAllRules();

        return b;
    }

    public static void grammar(LexerfulGrammarBuilder b) {

    }

    /**
     * Conditional Statements
     * https://docs.puppetlabs.com/puppet/3.7/reference/lang_conditional.html
     * @param b
     */
    public static void conditionalStatements(LexerfulGrammarBuilder b){
/*
        //b.rule(CONDITIONAL_STMT).is(b.firstOf(
        //        IF_STMT,
        //        UNLESS_STMT));
        b.rule(STATEMENT).is(IF_STATEMENT);
        b.rule(STATEMENT).is(b.firstOf(
                EXPRESSION_STATEMENT,
                COMPOUND_STATEMENT,
                RETURN_STATEMENT,
                CONTINUE_STATEMENT,
                BREAK_STATEMENT,
                IF_STATEMENT,
                WHILE_STATEMENT,
                NO_COMPLEXITY_STATEMENT));
        b.rule(CONDITION_CLAUSE).is(LPAREN, EXPRESSION, RPAREN);
        b.rule(IF_STATEMENT).is(IF, CONDITION_CLAUSE, STATEMENT);
        */
    }

    /**
     * Expressions
     * https://docs.puppetlabs.com/puppet/latest/reference/lang_expressions.html
     * @param b
     */
    public static void expressions(LexerfulGrammarBuilder b){

        b.rule(EXPRESSION).is(COMP_OPERATOR);
        //<arithop> ::= "+" | "-" | "/" | "*" | "<<" | ">>"
        b.rule(SHIFT_OPER).is(b.firstOf(
                RSHIFT,
                LSHIFT));

        b.rule(A_OPER).is(b.firstOf(
                PLUS,
                MINUS));

        b.rule(M_OPER).is(b.firstOf(
                MUL,
                DIV));

        //<boolop>  ::= "and" | "or"

        //<compop>  ::= "==" | "!=" | ">" | ">=" | "<=" | "<"
        b.rule(COMP_OPERATOR).is(b.firstOf(
                ISEQUAL,
                NOTEQUAL,
                GREATERTHAN,
                GREATEREQUAL,
                LESSEQUAL,
                LESSTHAN));

        //<matchop>  ::= "=~" | "!~"
        b.rule(MATCH_OPERATOR).is(b.firstOf(
                MATCH,
                NOMATCH));

        //<literals> ::= <float> | <integer> | <hex-integer> | <octal-integer> | <quoted-string>

    }
}
