/**
 * Sonar Puppet Plugin
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Iain Adams
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
    ATTRIBUTE,
    DATA_TYPE,

    //EXPRESSIONS
    EXPRESSION,
    ARITH_EXP,
    BOOL_EXPRESSION,
    COMP_EXP,
    MATCH_EXPRESSION,
    UNARY_NOT_EXPRESSION,
    UNARY_NEG_EXPRESSION,
    BRACKET_EXP,

    ATOM,

    SHIFT_EXPRESSION,
    ADDITIVE_EXPRESSION,
    MULTIPLICATIVE_EXPRESSION,

    COMPARISON,

    ARITH_OP,
    ASSIGNMENT_EXPRESSION,
    BOOL_OPERATOR,
    COMP_OPERATOR,
    A_OPER,
    M_OPER,
    MATCH_OPERATOR,

    SHIFT_OPER,

    RIGHT_VALUE,
    LITERALS,
    ARGUMENT_EXPRESSION_LIST,
    NAME,
    NAMESPACE_SEP,
    QUALIFIED_IDENTIFIER,

    //SIMPLE STATEMENTS
    SIMPLE_STMT,
    STATEMENT,
    RESOURCE,
    RESOURCE_NAME,
    RESOURCE_REF,
    EXEC_RESOURCE,
    DEFINE_STMT,
    DEFINE_NAME,
    VIRTUALRESOURCE,
    COLLECTION,
    FUNC_CALL,
    PARAM_LIST,
    PARAMETER,
    NODE_STMT,
    NODE_NAME,
    INCLUDE_STMT,
    ARRAY,
    HASHES,
    HASH_KEY,
    CLASS_RESOURCE_REF,
    RELATIONSHIP_STMT,
    RELATIONSHIP_LR_STMT,
    RELATIONSHIP_RL_STMT,
    ACCESSOR,

    RESOURCE_COLLECTOR,
    RESOURCE_COLLECTOR_SEARCH,
    COLLECTOR_EQ_SEARCH,
    COLLECTOR_NOTEQ_SEARCH,
    COLLECTOR_AND_SEARCH,
    COLLECTOR_OR_SEARCH,
    EXPORTED_RESOURCE_COLLECTOR,

    //CONDITIONAL STATEMENTS
    CONDITION_CLAUSE,

    // Compound statements
    COMPOUND_STMT,
    CLASSDEF,
    CLASSNAME,
    IF_STMT,
    ELSEIF_STMT,
    UNLESS_STMT,
    CASE_STMT,
    CASE_MATCHER,
    CASES,
    SELECTOR_STMT,
    SELECTOR_CASE,
    CONTROL_VAR,

    // Top-level components

    FILE_INPUT;

    public static LexerfulGrammarBuilder create() {
        LexerfulGrammarBuilder b = LexerfulGrammarBuilder.create();

        b.rule(FILE_INPUT).is(b.zeroOrMore(b.firstOf(NEWLINE, STATEMENT)), EOF);
        b.rule(STATEMENT).is(b.firstOf(RESOURCE, SIMPLE_STMT, EXPRESSION, COMPOUND_STMT));

        grammar(b);
        conditionalStatements(b);
        compoundStatements(b);
        simpleStatements(b);
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

        b.rule(ATTRIBUTE).is(b.firstOf(IDENTIFIER,
                        NOTIFY,
                        REQUIRE,
                        BEFORE,
                        UNLESS,
                        SUBSCRIBE),
                FARROW,
                b.firstOf(SELECTOR_STMT, EXPRESSION, RESOURCE_REF, LITERALS, IDENTIFIER, TRUE, FALSE),
                b.optional(COMMA));

        b.rule(RESOURCE).is(QUALIFIED_IDENTIFIER,
                LBRACE,
                b.optional(RESOURCE_NAME, COLON),
                b.zeroOrMore(ATTRIBUTE),
                RBRACE);
        b.rule(RESOURCE_NAME).is(b.firstOf(ARRAY, LITERAL, IDENTIFIER, VARIABLE));

        b.rule(DATA_TYPE).is(b.firstOf(TRUE,
                FALSE,
                LITERAL,
                INTEGER,
                HEX_INTEGER,
                OCTAL_INTEGER,
                FLOAT,
                UNDEF,
                ARRAY,
                HASHES,
                IDENTIFIER,
                VARIABLE
                ));
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
        b.rule(STATEMENT).is(IF_STMT);
        b.rule(STATEMENT).is(b.firstOf(
                EXPRESSION_STATEMENT,
                COMPOUND_STATEMENT,
                RETURN_STATEMENT,
                CONTINUE_STATEMENT,
                BREAK_STATEMENT,
                IF_STMT,
                WHILE_STATEMENT,
                NO_COMPLEXITY_STATEMENT));
        b.rule(CONDITION_CLAUSE).is(LPAREN, EXPRESSION, RPAREN);
        b.rule(IF_STMT).is(IF, CONDITION_CLAUSE, STATEMENT);
        */
    }

    /**
     * Simple Statements
     *
     * @param b
     */
    public static void simpleStatements(LexerfulGrammarBuilder b) {
        b.rule(SIMPLE_STMT).is(b.firstOf(
                DEFINE_STMT,
                NODE_STMT,
                INCLUDE_STMT,
                RELATIONSHIP_STMT));

        b.rule(DEFINE_STMT).is(DEFINE,
                DEFINE_NAME,
                b.optional(PARAM_LIST),
                LBRACE,
                b.zeroOrMore(STATEMENT),
                RBRACE);

        b.rule(DEFINE_NAME).is(QUALIFIED_IDENTIFIER);

        b.rule(PARAM_LIST).is(LPAREN,
                b.zeroOrMore(PARAMETER, b.optional(COMMA)),
                RPAREN);

        b.rule(PARAMETER).is(VARIABLE,
                b.optional(EQUALS, DATA_TYPE));

        b.rule(NODE_STMT).is(NODE,
                NODE_NAME,
                b.zeroOrMore(COMMA, b.firstOf(LITERAL, IDENTIFIER)),
                b.optional(INHERITS, LITERAL),
                LBRACE,
                b.optional(b.zeroOrMore(STATEMENT)),
                RBRACE);

        b.rule(NODE_NAME).is(b.firstOf(LITERAL, IDENTIFIER));

        b.rule(INCLUDE_STMT).is("include", b.firstOf(LITERAL, QUALIFIED_IDENTIFIER));

        b.rule(HASHES).is(LBRACE,
                b.zeroOrMore(HASH_KEY, FARROW, DATA_TYPE, b.optional(COMMA)),
                RBRACE,
                b.optional(COMMA));

        b.rule(HASH_KEY).is(b.firstOf(IDENTIFIER, LITERAL));

        b.rule(ARRAY).is(LBRACK,
                b.zeroOrMore(DATA_TYPE,
                        b.zeroOrMore(COMMA, DATA_TYPE)),
                b.optional(COMMA),
                RBRACK);

        b.rule(RESOURCE_REF).is(QUALIFIED_IDENTIFIER, ARRAY);

        b.rule(RELATIONSHIP_STMT).is(b.firstOf(RELATIONSHIP_LR_STMT, RELATIONSHIP_RL_STMT));
        b.rule(RELATIONSHIP_LR_STMT).is(RESOURCE_REF, b.oneOrMore(b.firstOf(IN_EDGE, IN_EDGE_SUB), b.firstOf(RESOURCE_REF,RESOURCE_COLLECTOR)));
        b.rule(RELATIONSHIP_RL_STMT).is(RESOURCE_REF, b.oneOrMore(b.firstOf(OUT_EDGE, OUT_EDGE_SUB), b.firstOf(RESOURCE_REF,RESOURCE_COLLECTOR)));

        b.rule(ACCESSOR).is(VARIABLE, b.oneOrMore(LBRACK, b.firstOf(LITERAL, INTEGER, IDENTIFIER), RBRACK));
    }

    /**
     * Compound Statements
     *
     * @param b
     */
    public static void compoundStatements(LexerfulGrammarBuilder b) {
        b.rule(COMPOUND_STMT).is(b.firstOf(CLASSDEF,
                CLASS_RESOURCE_REF,
                IF_STMT,
                CASE_STMT,
                RESOURCE_COLLECTOR,
                EXPORTED_RESOURCE_COLLECTOR));

        b.rule(CLASSDEF).is(CLASS,
                            CLASSNAME,
                            b.optional(PARAM_LIST),
                            b.optional(ASSIGNMENT_EXPRESSION),
                            b.optional(INHERITS, CLASSNAME),
                            LBRACE,
                            b.zeroOrMore(STATEMENT),
                            RBRACE);
        //https://docs.puppetlabs.com/puppet/3.8/reference/lang_classes.html#using-resource-like-declarations
        b.rule(CLASS_RESOURCE_REF).is(
                CLASS,
                LBRACE,
                LITERAL,
                COLON,
                b.zeroOrMore(ATTRIBUTE),
                RBRACE);

        b.rule(CLASSNAME).is(QUALIFIED_IDENTIFIER);

        b.rule(IF_STMT).is(IF,
                CONDITION,
                LBRACE,
                b.zeroOrMore(STATEMENT),
                RBRACE,
                b.zeroOrMore(ELSEIF_STMT),
                b.optional(ELSE, LBRACE, b.zeroOrMore(STATEMENT), RBRACE));

        b.rule(ELSEIF_STMT).is(ELSIF, CONDITION, LBRACE, b.zeroOrMore(STATEMENT), RBRACE);

        b.rule(CASE_STMT).is(CASE, b.firstOf(VARIABLE, EXPRESSION), LBRACE,
                b.zeroOrMore(CASE_MATCHER),
                RBRACE);
        b.rule(CASE_MATCHER).is(CASES, COLON, LBRACE, b.zeroOrMore(STATEMENT), RBRACE);
        b.rule(CASES).is(b.firstOf(TRUE, FALSE, NAME, DEFAULT, LITERAL, VARIABLE, FUNC_CALL, REGULAR_EXPRESSION_LITERAL),
                         b.zeroOrMore(COMMA, b.firstOf(TRUE, FALSE, NAME, DEFAULT, LITERAL, VARIABLE, FUNC_CALL, REGULAR_EXPRESSION_LITERAL)));

        b.rule(SELECTOR_STMT).is(
                CONTROL_VAR,
                QMARK,
                LBRACE,
                b.oneOrMore(SELECTOR_CASE),
                b.optional(
                        DEFAULT,
                        FARROW,
                        b.firstOf(TRUE, FALSE, UNDEF, FUNC_CALL, RESOURCE_REF, NAME, LITERAL, SELECTOR_STMT, VARIABLE, ARRAY),
                        b.optional(COMMA)),
                RBRACE);

        b.rule(SELECTOR_CASE).is(
                b.firstOf(TRUE, FALSE, UNDEF, NAME, LITERAL, VARIABLE, FUNC_CALL, REGULAR_EXPRESSION_LITERAL),
                FARROW,
                b.firstOf(TRUE, FALSE, UNDEF, FUNC_CALL, NAME, LITERAL, SELECTOR_STMT, VARIABLE, ARRAY),
                b.optional(COMMA));

        b.rule(CONTROL_VAR).is(b.firstOf(VARIABLE, FUNC_CALL));

        b.rule(RESOURCE_COLLECTOR).is(QUALIFIED_IDENTIFIER, LCOLLECT, b.optional(RESOURCE_COLLECTOR_SEARCH), RCOLLECT);
        b.rule(RESOURCE_COLLECTOR_SEARCH).is(
                b.firstOf(COLLECTOR_AND_SEARCH,
                        COLLECTOR_OR_SEARCH,
                        COLLECTOR_EQ_SEARCH,
                        COLLECTOR_NOTEQ_SEARCH));

        b.rule(COLLECTOR_EQ_SEARCH).is(
                IDENTIFIER,
                ISEQUAL,
                b.firstOf(VARIABLE, LITERALS, TRUE, FALSE, RESOURCE_REF, UNDEF));

        b.rule(COLLECTOR_NOTEQ_SEARCH).is(
                IDENTIFIER,
                NOTEQUAL,
                b.firstOf(VARIABLE, LITERALS, TRUE, FALSE, RESOURCE_REF, UNDEF));

        b.rule(COLLECTOR_AND_SEARCH).is(
                b.firstOf(COLLECTOR_EQ_SEARCH, COLLECTOR_NOTEQ_SEARCH),
                AND,
                b.firstOf(COLLECTOR_EQ_SEARCH, COLLECTOR_NOTEQ_SEARCH));

        b.rule(COLLECTOR_OR_SEARCH).is(
                b.firstOf(COLLECTOR_EQ_SEARCH, COLLECTOR_NOTEQ_SEARCH),
                OR,
                b.firstOf(COLLECTOR_EQ_SEARCH, COLLECTOR_NOTEQ_SEARCH));

        b.rule(EXPORTED_RESOURCE_COLLECTOR).is(QUALIFIED_IDENTIFIER, LLCOLLECT, b.optional(RESOURCE_COLLECTOR_SEARCH), RRCOLLECT);
    }

    /**
     * Expressions
     * https://docs.puppetlabs.com/puppet/latest/reference/lang_expressions.html
     * @param b
     */
    public static void expressions(LexerfulGrammarBuilder b){

        b.rule(CONDITION).is(ASSIGNMENT_EXPRESSION);

        b.rule(OPERAND).is(b.firstOf(
                LITERALS,
                VARIABLE,
                FUNC_CALL,
                TRUE,
                FALSE,
                UNDEF));

        /*<exp> ::=  <exp> <arithop> <exp>
                | <exp> <boolop> <exp>
                | <exp> <compop> <exp>
                | <exp> <matchop> <regex>
                | ! <exp>
                | - <exp>
                | "(" <exp> ")"
                | <rightvalue>*/
        b.rule(EXPRESSION).is(b.firstOf(
                ACCESSOR,
                ASSIGNMENT_EXPRESSION,
                RIGHT_VALUE,
                RESOURCE_REF));

        //https://docs.puppetlabs.com/puppet/latest/reference/lang_expressions.html#order-of-operations



        b.rule(UNARY_NOT_EXPRESSION).is(b.optional(NOT), ATOM);
        b.rule(UNARY_NEG_EXPRESSION).is(b.optional(MINUS), UNARY_NOT_EXPRESSION);

        b.rule(MATCH_EXPRESSION).is(UNARY_NEG_EXPRESSION, b.zeroOrMore(MATCH_OPERATOR, UNARY_NEG_EXPRESSION));
        b.rule(MULTIPLICATIVE_EXPRESSION).is(MATCH_EXPRESSION, b.zeroOrMore(M_OPER, MATCH_EXPRESSION));
        b.rule(ADDITIVE_EXPRESSION).is(MULTIPLICATIVE_EXPRESSION, b.zeroOrMore(A_OPER, MULTIPLICATIVE_EXPRESSION));
        b.rule(SHIFT_EXPRESSION).is(ADDITIVE_EXPRESSION, b.zeroOrMore(SHIFT_OPER, ADDITIVE_EXPRESSION));
        b.rule(COMPARISON).is(SHIFT_EXPRESSION, b.zeroOrMore(COMP_OPERATOR, SHIFT_EXPRESSION));
        b.rule(BOOL_EXPRESSION).is(COMPARISON, b.zeroOrMore(BOOL_OPERATOR, COMPARISON));
        b.rule(ASSIGNMENT_EXPRESSION).is(BOOL_EXPRESSION, b.zeroOrMore(EQUALS, BOOL_EXPRESSION));

        b.rule(ATOM).is(b.firstOf(
                b.sequence(LPAREN, ASSIGNMENT_EXPRESSION, RPAREN),
                SELECTOR_STMT,
                ARRAY,
                REGULAR_EXPRESSION_LITERAL,
                RESOURCE_REF,
                LITERALS,
                VARIABLE,
                FUNC_CALL,
                TRUE,
                FALSE,
                UNDEF
        ));



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
                DIV,
                MODULO));

        //<boolop>  ::= "and" | "or"
        b.rule(BOOL_OPERATOR).is(b.firstOf(
                AND,
                OR));

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
                LITERALS,
                ARRAY));

        //<literals> ::= <float> | <integer> | <hex-integer> | <octal-integer> | <quoted-string>
        b.rule(LITERALS).is(b.firstOf(
                FLOAT,
                INTEGER,
                HEX_INTEGER,
                OCTAL_INTEGER,
                LITERAL));

        //<regex> ::= '/regex/'



        b.rule(NAMESPACE_SEP).is(COLON, COLON);
        b.rule(QUALIFIED_IDENTIFIER).is(b.optional(NAMESPACE_SEP), IDENTIFIER, b.zeroOrMore(NAMESPACE_SEP, IDENTIFIER));
    }
}
