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
    ATTRIBUTE,
    DATA_TYPE,

    //EXPRESSIONS
    EXPRESSION,
    BOOL_EXPRESSION,
    MATCH_EXPRESSION,
    UNARY_NOT_EXPRESSION,
    UNARY_NEG_EXPRESSION,

    IN_EXPRESSION,

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
    RESOURCE_BODY,
    RESOURCE_REF,
    RESOURCE_DEFAULT_STMT,
    CLASS_REF,
    EXEC_RESOURCE,
    DEFINE_STMT,
    DEFINE_NAME,
    REQUIRE_STMT,
    CONTAIN_STMT,
    VIRTUALRESOURCE,
    COLLECTION,
    FUNC_CALL,
    PARAM_LIST,
    PARAMETER,
    NODE_STMT,
    NODE_NAME,
    INCLUDE_STMT,
    ARRAY,
    ARRAY_SECTIONING_STMT,
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
        b.rule(STATEMENT).is(b.firstOf(SIMPLE_STMT, COMPOUND_STMT, EXPRESSION));

        grammar(b);
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
                b.optional(LPAREN),
                ARGUMENT_EXPRESSION_LIST,
                b.optional(RPAREN));

        b.rule(ARGUMENT_EXPRESSION_LIST).is(EXPRESSION, b.zeroOrMore(COMMA, EXPRESSION), b.optional(COMMA));

        b.rule(ATTRIBUTE).is(b.firstOf(IDENTIFIER,
                        NOTIFY,
                        REQUIRE,
                        BEFORE,
                        UNLESS,
                        SUBSCRIBE),
                FARROW,
                b.firstOf(SELECTOR_STMT, EXPRESSION, RESOURCE_REF, LITERALS, IDENTIFIER, TRUE, FALSE),
                b.optional(COMMA));

        //https://docs.puppetlabs.com/puppet/latest/reference/lang_resources_advanced.html#full-syntax
        b.rule(RESOURCE).is(QUALIFIED_IDENTIFIER,
                LBRACE,
                b.oneOrMore(RESOURCE_BODY, b.optional(SEMIC)),
                RBRACE);
        b.rule(RESOURCE_NAME).is(b.firstOf(ARRAY, DEFAULT, LITERAL, IDENTIFIER, VARIABLE));

        b.rule(RESOURCE_BODY).is(
                RESOURCE_NAME, b.optional(COLON),
                b.zeroOrMore(ATTRIBUTE));

        b.rule(DATA_TYPE).is(b.firstOf(TRUE,
                FALSE,
                UNDEF,
                LITERAL,
                INTEGER,
                HEX_INTEGER,
                OCTAL_INTEGER,
                FLOAT,
                ARRAY,
                HASHES,
                IDENTIFIER,
                VARIABLE
        ));
    }

    /**
     * Simple Statements
     *
     * @param b
     */
    public static void simpleStatements(LexerfulGrammarBuilder b) {
        b.rule(SIMPLE_STMT).is(b.firstOf(
                RELATIONSHIP_STMT,
                RESOURCE,
                RESOURCE_DEFAULT_STMT,
                DEFINE_STMT,
                NODE_STMT,
                INCLUDE_STMT,
                REQUIRE_STMT,
                ARRAY_SECTIONING_STMT));

        b.rule(DEFINE_STMT).is(DEFINE,
                DEFINE_NAME,
                b.optional(PARAM_LIST),
                LBRACE,
                b.zeroOrMore(STATEMENT),
                RBRACE);

        b.rule(DEFINE_NAME).is(QUALIFIED_IDENTIFIER);

        b.rule(CLASS_REF).is("Class", ARRAY);

        //https://docs.puppetlabs.com/puppet/latest/reference/lang_classes.html#using-require
        b.rule(REQUIRE_STMT).is(REQUIRE,
                b.firstOf(
                        ARRAY,
                        b.sequence(CLASS_REF, b.zeroOrMore(COMMA, CLASS_REF)),
                        b.sequence(CLASSNAME, b.zeroOrMore(COMMA, CLASSNAME))
                ));

        //https://docs.puppetlabs.com/puppet/latest/reference/lang_classes.html#using-contain
        b.rule(CONTAIN_STMT).is("contain",
                b.firstOf(
                        ARRAY,
                        b.sequence(CLASS_REF, b.zeroOrMore(COMMA, CLASS_REF)),
                        b.sequence(CLASSNAME, b.zeroOrMore(COMMA, CLASSNAME))
                ));

        b.rule(PARAM_LIST).is(LPAREN,
                b.zeroOrMore(PARAMETER, b.optional(COMMA)),
                RPAREN);

        b.rule(PARAMETER).is(VARIABLE,
                b.optional(EQUALS, b.firstOf(FUNC_CALL, DATA_TYPE)));

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
                b.zeroOrMore(HASH_KEY, FARROW, b.firstOf(SELECTOR_STMT, DATA_TYPE), b.optional(COMMA)),
                RBRACE,
                b.optional(COMMA));

        b.rule(HASH_KEY).is(b.firstOf(IDENTIFIER, LITERAL, REQUIRE));

        b.rule(ARRAY).is(LBRACK,
                b.zeroOrMore(b.firstOf(FUNC_CALL, DATA_TYPE),
                        b.zeroOrMore(COMMA, b.firstOf(FUNC_CALL, DATA_TYPE))),
                b.optional(COMMA),
                RBRACK);

        //https://docs.puppetlabs.com/puppet/latest/reference/lang_data_array.html#array-sectioning
        b.rule(ARRAY_SECTIONING_STMT).is(VARIABLE, LBRACK, INTEGER, COMMA, INTEGER, RBRACK);

        b.rule(RESOURCE_REF).is(QUALIFIED_IDENTIFIER, ARRAY);

        b.rule(RESOURCE_DEFAULT_STMT).is(
                QUALIFIED_IDENTIFIER,
                LBRACE,
                b.zeroOrMore(ATTRIBUTE),
                RBRACE);

        b.rule(RELATIONSHIP_STMT).is(b.firstOf(RELATIONSHIP_LR_STMT, RELATIONSHIP_RL_STMT));
        b.rule(RELATIONSHIP_LR_STMT).is(
                b.firstOf(RESOURCE, RESOURCE_REF, RESOURCE_COLLECTOR, CLASS_RESOURCE_REF),
                b.oneOrMore(
                        b.firstOf(IN_EDGE, IN_EDGE_SUB),
                        b.firstOf(RESOURCE_REF,RESOURCE_COLLECTOR, RESOURCE, CLASS_RESOURCE_REF)
                ));
        b.rule(RELATIONSHIP_RL_STMT).is(
                b.firstOf(RESOURCE, RESOURCE_REF, RESOURCE_COLLECTOR, CLASS_RESOURCE_REF),
                b.oneOrMore(
                        b.firstOf(OUT_EDGE, OUT_EDGE_SUB),
                        b.firstOf(RESOURCE_REF,RESOURCE_COLLECTOR, RESOURCE, CLASS_RESOURCE_REF)
                ));

        b.rule(ACCESSOR).is(VARIABLE, b.oneOrMore(LBRACK, b.firstOf(LITERAL, INTEGER, IDENTIFIER), RBRACK));
    }

    /**
     * Compound Statements
     *
     * @param b
     */
    public static void compoundStatements(LexerfulGrammarBuilder b) {
        b.rule(COMPOUND_STMT).is(b.firstOf(
                CLASSDEF,
                CLASS_RESOURCE_REF,
                IF_STMT,
                CASE_STMT,
                RESOURCE_COLLECTOR,
                EXPORTED_RESOURCE_COLLECTOR));

        b.rule(CLASSDEF).is(CLASS,
                            CLASSNAME,
                            b.optional(PARAM_LIST),
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
                b.zeroOrMore(SELECTOR_CASE),
                b.optional(
                        DEFAULT,
                        FARROW,
                        b.firstOf(TRUE, FALSE, UNDEF, SELECTOR_STMT, FUNC_CALL, RESOURCE_REF, NAME, LITERAL, VARIABLE, ARRAY),
                        b.optional(COMMA)),
                RBRACE);

        b.rule(SELECTOR_CASE).is(
                b.firstOf(DATA_TYPE, NAME, FUNC_CALL, REGULAR_EXPRESSION_LITERAL),
                FARROW,
                b.firstOf(SELECTOR_STMT, FUNC_CALL, DATA_TYPE),
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



        b.rule(UNARY_NOT_EXPRESSION).is(b.optional(NOT), ATOM).skipIfOneChild();
        b.rule(UNARY_NEG_EXPRESSION).is(b.optional(MINUS), UNARY_NOT_EXPRESSION).skipIfOneChild();

        b.rule(IN_EXPRESSION).is(UNARY_NEG_EXPRESSION, b.zeroOrMore(IN, UNARY_NEG_EXPRESSION)).skipIfOneChild();

        b.rule(MATCH_EXPRESSION).is(IN_EXPRESSION, b.zeroOrMore(MATCH_OPERATOR, IN_EXPRESSION)).skipIfOneChild();
        b.rule(MULTIPLICATIVE_EXPRESSION).is(MATCH_EXPRESSION, b.zeroOrMore(M_OPER, MATCH_EXPRESSION)).skipIfOneChild();
        b.rule(ADDITIVE_EXPRESSION).is(MULTIPLICATIVE_EXPRESSION, b.zeroOrMore(A_OPER, MULTIPLICATIVE_EXPRESSION)).skipIfOneChild();
        b.rule(SHIFT_EXPRESSION).is(ADDITIVE_EXPRESSION, b.zeroOrMore(SHIFT_OPER, ADDITIVE_EXPRESSION)).skipIfOneChild();
        b.rule(COMPARISON).is(SHIFT_EXPRESSION, b.zeroOrMore(COMP_OPERATOR, SHIFT_EXPRESSION)).skipIfOneChild();
        b.rule(BOOL_EXPRESSION).is(COMPARISON, b.zeroOrMore(BOOL_OPERATOR, COMPARISON)).skipIfOneChild();
        b.rule(ASSIGNMENT_EXPRESSION).is(BOOL_EXPRESSION, b.zeroOrMore(EQUALS, BOOL_EXPRESSION)).skipIfOneChild();

        b.rule(ATOM).is(b.firstOf(
                b.sequence(LPAREN, ASSIGNMENT_EXPRESSION, RPAREN),
                SELECTOR_STMT,
                REGULAR_EXPRESSION_LITERAL,
                RESOURCE_REF,
                LITERALS,
                VARIABLE,
                FUNC_CALL,
                TRUE,
                FALSE,
                UNDEF,
                ARRAY,
                HASHES
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
                LITERAL)).skipIfOneChild();

        //<regex> ::= '/regex/'



        b.rule(NAMESPACE_SEP).is(COLON, COLON);
        b.rule(QUALIFIED_IDENTIFIER).is(b.optional(NAMESPACE_SEP), IDENTIFIER, b.zeroOrMore(NAMESPACE_SEP, IDENTIFIER));
    }
}
