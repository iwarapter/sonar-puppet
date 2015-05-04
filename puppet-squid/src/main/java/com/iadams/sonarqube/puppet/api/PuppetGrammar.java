package com.iadams.sonarqube.puppet.api;

import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.grammar.LexerfulGrammarBuilder;

import static com.iadams.sonarqube.puppet.api.PuppetKeyword.*;
import static com.iadams.sonarqube.puppet.api.PuppetPunctuator.*;
import static com.iadams.sonarqube.puppet.api.PuppetTokenType.*;
import static com.iadams.sonarqube.puppet.api.PuppetTokenType.NEWLINE;
import static com.iadams.sonarqube.puppet.api.PuppetTokenType.VARIABLE;
import static com.sonar.sslr.api.GenericTokenType.*;
import static com.sonar.sslr.api.GenericTokenType.LITERAL;
import static com.sonar.sslr.api.GenericTokenType.EOF;

/**
 * Created by iwarapter
 */
public enum PuppetGrammar  implements GrammarRuleKey {

    CONDITION,
    OPERAND,

    //EXPRESSIONS
    EXPRESSION,
    ARITH_EXP,
    BOOL_EXP,
    COMP_EXP,
    MATCH_EXP,
    NOT_EXP,
    MINUS_EXP,
    BRACKET_EXP,

    ARITH_OP,
    ASSIGNMENT_EXPRESSION,
    BOOL_OPERATOR,
    COMP_OPERATOR,
    A_OPER,
    M_OPER,
    MATCH_OPERATOR,

    SHIFT_OPER,

    RIGHT_VALUE,
    LITERAL_LIST,
    ARGUMENT_EXPRESSION_LIST,
    NAME,

    //SIMPLE STATEMENTS
    STATEMENT,
    RESOURCE,
    VIRTUALRESOURCE,
    COLLECTION,
    FUNC_CALL,

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

        b.rule(NAME).is(IDENTIFIER);

        b.rule(FUNC_CALL).is(
                NAME,
                LPAREN,
                ARGUMENT_EXPRESSION_LIST,
                RPAREN);

        b.rule(ARGUMENT_EXPRESSION_LIST).is(EXPRESSION, b.zeroOrMore(COMMA, EXPRESSION));
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

        b.rule(OPERAND).is(b.firstOf(
                LITERAL_LIST,
                VARIABLE,
                TRUE,
                FALSE));

        /*<exp> ::=  <exp> <arithop> <exp>
                | <exp> <boolop> <exp>
                | <exp> <compop> <exp>
                | <exp> <matchop> <regex>
                | ! <exp>
                | - <exp>
                | "(" <exp> ")"
                | <rightvalue>*/
        b.rule(EXPRESSION).is(b.firstOf(
                ARITH_EXP,
                BOOL_EXP,
                COMP_EXP,
                MATCH_EXP,
                NOT_EXP,
                MINUS_EXP,
                BRACKET_EXP,
                RIGHT_VALUE));

        b.rule(ARITH_EXP).is(OPERAND, ARITH_OP, OPERAND);
        b.rule(BOOL_EXP).is(OPERAND, BOOL_OPERATOR, OPERAND);
        b.rule(COMP_EXP).is(OPERAND, COMP_OPERATOR, OPERAND);
        b.rule(MATCH_EXP).is(OPERAND, MATCH_OPERATOR, OPERAND);
        b.rule(NOT_EXP).is(NOT, EXPRESSION);
        b.rule(MINUS_EXP).is(MINUS, OPERAND);
        b.rule(BRACKET_EXP).is(LPAREN, EXPRESSION, RPAREN);

        //<arithop> ::= "+" | "-" | "/" | "*" | "<<" | ">>"
        b.rule(ARITH_OP).is(b.firstOf(
                SHIFT_OPER,
                A_OPER,
                M_OPER));

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
        b.rule(BOOL_OPERATOR).is(b.firstOf(
                AND,
                OR,
                NOT));

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

        //<rightvalue> ::= <variable> | <function-call> | <literals>
        b.rule(RIGHT_VALUE).is(b.firstOf(
                FUNC_CALL,
                VARIABLE,
                LITERAL_LIST));

        //<literals> ::= <float> | <integer> | <hex-integer> | <octal-integer> | <quoted-string>
        b.rule(LITERAL_LIST).is(b.firstOf(
                INTEGER,
                HEX_INTEGER,
                OCTAL_INTEGER,
                LITERAL));

        //<regex> ::= '/regex/'

    }
}
