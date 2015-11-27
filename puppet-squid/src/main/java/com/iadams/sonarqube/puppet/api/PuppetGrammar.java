/*
 * SonarQube Puppet Plugin
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Iain Adams and David RACODON
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
import static com.sonar.sslr.api.GenericTokenType.EOF;

/*
 * See grammar definition by Puppet Labs: https://github.com/puppetlabs/puppet/blob/3.x/lib/puppet/parser/grammar.ra
 */
public enum PuppetGrammar implements GrammarRuleKey {

  QUOTED_TEXT,
  KEYWORD,

  PARAMS,
  PARAM,
  PARAM_NAME,
  ADD_PARAM,
  ANY_PARAMS,
  ANY_PARAM,

  TYPE,

  EXPRESSION,
  EXPRESSIONS,
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
  ASSIGNMENT,
  APPENDS_STMT,
  BOOL_OPERATOR,
  COMP_OPERATOR,
  A_OPER,
  M_OPER,
  MATCH_OPERATOR,

  SHIFT_OPER,

  RIGHT_VALUE,
  FUNCVALUES,
  FUNCRVALUE,

  NUMBER,

  // SIMPLE STATEMENTS
  SIMPLE_STMT,
  STATEMENT,
  RESOURCE,
  RESOURCE_NAME,
  RESOURCE_INST,
  RESOURCE_INSTANCES,
  RESOURCE_OVERRIDE,

  EXPORTED_RESOURCE,
  VIRTUAL_RESOURCE,

  RESOURCE_REF,
  REQUIRE_STMT,
  DEFINITION,
  COLLECTION,
  UNLESS_STMT,
  IMPORT_STMT,
  FUNCTION_STMT,
  ARGUMENT_LIST,
  ARGUMENTS,
  ARGUMENT,
  ARRAY,
  HASH,
  HASH_PAIRS,
  HASH_PAIR,
  KEY,

  RELATIONSHIP,
  RELATIONSHIP_SIDE,
  EDGE,

  HASH_ARRAY_ACCESS,
  HASH_ARRAY_ACCESSES,

  COLLECTOR_VAL,
  COLLECTOR,
  COLLECT_EXPR,
  COLLECT_JOIN,
  COLLECT_STMT,
  COLLECT_STMTS,

  NODE_DEFINITION,
  HOST_MATCHES,
  HOST_MATCH,

  // Compound statements
  COMPOUND_STMT,
  CLASSDEF,
  CLASSNAME,
  CLASSNAME_OR_DEFAULT,
  CLASS_PARENT,
  IF_STMT,
  ELSIF_STMT,
  ELSE_STMT,
  CASE_STMT,
  CASE_MATCHER,
  CASE_VALUES,

  SELECTOR,
  SVALUES,
  SINTVALUES,
  SELECTVAL,
  SELECTLHAND,

  // Top-level components
  FILE_INPUT;

  public static LexerfulGrammarBuilder create() {
    LexerfulGrammarBuilder b = LexerfulGrammarBuilder.create();

    b.rule(FILE_INPUT).is(b.optional(BOM), b.zeroOrMore(b.firstOf(NEWLINE, STATEMENT)), EOF);
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

    b.rule(FUNCTION_STMT).is(
      b.firstOf(
        b.sequence(NAME, LPAREN, EXPRESSIONS, b.optional(COMMA), RPAREN),
        b.sequence(NAME, LPAREN, RPAREN),
        b.sequence(NAME, FUNCVALUES)
        ));

    b.rule(FUNCVALUES).is(
      RIGHT_VALUE,
      b.zeroOrMore(COMMA, RIGHT_VALUE));

    b.rule(FUNCRVALUE).is(
      NAME,
      LPAREN,
      b.optional(EXPRESSIONS),
      RPAREN
      );

    b.rule(PARAM_NAME).is(b.firstOf(KEYWORD, NAME, TRUE, FALSE));

    b.rule(PARAM).is(
      PARAM_NAME,
      FARROW,
      EXPRESSION);

    b.rule(PARAMS).is(b.optional(
      PARAM,
      b.zeroOrMore(COMMA, PARAM)),
      b.optional(COMMA));

    b.rule(ADD_PARAM).is(NAME, PARROW, EXPRESSION);

    b.rule(ANY_PARAM).is(b.firstOf(PARAM, ADD_PARAM)).skip();

    b.rule(ANY_PARAMS).is(b.optional(
      ANY_PARAM,
      b.zeroOrMore(COMMA, ANY_PARAM)),
      b.optional(COMMA));

    b.rule(RESOURCE).is(b.firstOf(
      b.sequence(CLASSNAME, LBRACE, RESOURCE_INSTANCES, b.optional(SEMIC), RBRACE),
      b.sequence(TYPE, LBRACE, PARAMS, RBRACE)));

    b.rule(RESOURCE_INST).is(RESOURCE_NAME, COLON, PARAMS);

    b.rule(RESOURCE_INSTANCES).is(
      RESOURCE_INST,
      b.zeroOrMore(SEMIC, RESOURCE_INST)).skip();

    b.rule(RESOURCE_NAME).is(b.firstOf(
      SELECTOR,
      HASH_ARRAY_ACCESSES,
      ARRAY,
      DEFAULT,
      NAME,
      QUOTED_TEXT,
      VARIABLE,
      TYPE));

    b.rule(RESOURCE_OVERRIDE).is(
      RESOURCE_REF,
      LBRACE,
      ANY_PARAMS,
      RBRACE);

    b.rule(QUOTED_TEXT).is(
      b.firstOf(
        SINGLE_QUOTED_STRING_LITERAL,
        DOUBLE_QUOTED_STRING_LITERAL)).skip();

    b.rule(TYPE).is(REF);

    b.rule(KEYWORD).is(b.firstOf(
      AND,
      CASE,
      CLASS,
      DEFAULT,
      DEFINE,
      ELSE,
      ELSIF,
      IF,
      IN,
      IMPORT,
      INHERITS,
      NODE,
      OR,
      UNDEF,
      UNLESS));

  }

  /*
   * Simple Statements
   */
  public static void simpleStatements(LexerfulGrammarBuilder b) {

    b.rule(SIMPLE_STMT).is(b.firstOf(
      NODE_DEFINITION,
      RELATIONSHIP,
      ASSIGNMENT,
      APPENDS_STMT,
      RESOURCE,
      UNLESS_STMT,
      IMPORT_STMT,
      RESOURCE_OVERRIDE,
      DEFINITION,
      FUNCTION_STMT));

    b.rule(ASSIGNMENT).is(
      b.firstOf(HASH_ARRAY_ACCESS, VARIABLE),
      EQUALS,
      EXPRESSION);

    b.rule(APPENDS_STMT).is(
      VARIABLE,
      APPENDS,
      EXPRESSION);

    b.rule(DEFINITION).is(DEFINE,
      CLASSNAME,
      ARGUMENT_LIST,
      LBRACE,
      b.zeroOrMore(STATEMENT),
      RBRACE);

    b.rule(ARGUMENT_LIST).is(b.optional(b.firstOf(
      b.sequence(LPAREN, RPAREN),
      b.sequence(LPAREN, ARGUMENTS, RPAREN)
      ))).skip();

    b.rule(ARGUMENTS).is(
      ARGUMENT,
      b.zeroOrMore(COMMA, ARGUMENT),
      b.optional(COMMA));

    b.rule(ARGUMENT).is(b.firstOf(
      b.sequence(VARIABLE, EQUALS, EXPRESSION),
      VARIABLE
      ));

    b.rule(HASH).is(b.firstOf(
      b.sequence(LBRACE, HASH_PAIRS, RBRACE),
      b.sequence(LBRACE, RBRACE)));

    b.rule(HASH_PAIRS).is(
      HASH_PAIR,
      b.zeroOrMore(COMMA, HASH_PAIR),
      b.optional(COMMA));

    b.rule(HASH_PAIR).is(KEY, FARROW, EXPRESSION);

    b.rule(KEY).is(b.firstOf(NAME, QUOTED_TEXT));

    b.rule(ARRAY).is(b.firstOf(
      b.sequence(LBRACK, EXPRESSIONS, b.optional(COMMA), RBRACK),
      b.sequence(LBRACK, RBRACK)));

    b.rule(RESOURCE_REF).is(
      b.firstOf(NAME, TYPE), LBRACK, EXPRESSIONS, RBRACK);

    b.rule(RELATIONSHIP).is(
      RELATIONSHIP_SIDE,
      EDGE,
      RELATIONSHIP_SIDE,
      b.zeroOrMore(
        EDGE,
        RELATIONSHIP_SIDE
        )
      );

    b.rule(RELATIONSHIP_SIDE).is(b.firstOf(
      RESOURCE,
      RESOURCE_REF,
      COLLECTION,
      VARIABLE,
      QUOTED_TEXT,
      SELECTOR,
      CASE_STMT,
      HASH_ARRAY_ACCESSES
      ));

    b.rule(EDGE).is(b.firstOf(IN_EDGE, OUT_EDGE, IN_EDGE_SUB, OUT_EDGE_SUB)).skip();

    b.rule(HASH_ARRAY_ACCESS).is(VARIABLE, LBRACK, EXPRESSION, RBRACK);
    b.rule(HASH_ARRAY_ACCESSES).is(HASH_ARRAY_ACCESS, b.zeroOrMore(LBRACK, EXPRESSION, RBRACK));

    b.rule(NODE_DEFINITION).is(
      NODE,
      HOST_MATCHES,
      b.optional(INHERITS, HOST_MATCH),
      LBRACE,
      b.zeroOrMore(STATEMENT),
      RBRACE
      );

    b.rule(HOST_MATCHES).is(
      HOST_MATCH,
      b.zeroOrMore(COMMA, HOST_MATCH),
      b.optional(COMMA)
      ).skip();

    b.rule(HOST_MATCH).is(b.firstOf(
      REGULAR_EXPRESSION_LITERAL,
      NAME,
      SINGLE_QUOTED_STRING_LITERAL,
      DOUBLE_QUOTED_STRING_LITERAL,
      DEFAULT
      ));

    b.rule(UNLESS_STMT).is(UNLESS, EXPRESSION, LBRACE, b.zeroOrMore(STATEMENT), RBRACE);

    b.rule(IMPORT_STMT).is(IMPORT, QUOTED_TEXT, b.zeroOrMore(COMMA, QUOTED_TEXT));
  }

  /*
   * Compound Statements
   */
  public static void compoundStatements(LexerfulGrammarBuilder b) {
    b.rule(COMPOUND_STMT).is(b.firstOf(
      CLASSDEF,
      IF_STMT,
      CASE_STMT,
      COLLECTION,
      EXPORTED_RESOURCE,
      VIRTUAL_RESOURCE));

    b.rule(CLASSDEF).is(CLASS,
      CLASSNAME,
      ARGUMENT_LIST,
      b.optional(CLASS_PARENT),
      LBRACE,
      b.zeroOrMore(STATEMENT),
      RBRACE);

    b.rule(CLASSNAME).is(b.firstOf(NAME, CLASS));

    b.rule(CLASSNAME_OR_DEFAULT).is(b.firstOf(CLASSNAME, DEFAULT));

    b.rule(CLASS_PARENT).is(INHERITS, CLASSNAME_OR_DEFAULT);

    b.rule(IF_STMT).is(IF,
      EXPRESSIONS,
      LBRACE,
      b.zeroOrMore(STATEMENT),
      RBRACE,
      b.zeroOrMore(ELSIF_STMT),
      b.optional(ELSE_STMT));

    b.rule(ELSIF_STMT).is(ELSIF, EXPRESSIONS, LBRACE, b.zeroOrMore(STATEMENT), RBRACE);

    b.rule(ELSE_STMT).is(ELSE, LBRACE, b.zeroOrMore(STATEMENT), RBRACE);

    b.rule(CASE_STMT).is(CASE, EXPRESSION, LBRACE,
      b.oneOrMore(CASE_MATCHER),
      RBRACE);
    b.rule(CASE_MATCHER).is(CASE_VALUES, COLON, LBRACE, b.zeroOrMore(STATEMENT), RBRACE);
    b.rule(CASE_VALUES).is(SELECTLHAND, b.zeroOrMore(COMMA, SELECTLHAND));

    b.rule(EXPORTED_RESOURCE).is(AT, AT, RESOURCE);

    b.rule(VIRTUAL_RESOURCE).is(AT, RESOURCE);

    /*
     * Collections
     */
    b.rule(COLLECTION).is(b.firstOf(
      b.sequence(TYPE, COLLECTOR, LBRACE, ANY_PARAMS, RBRACE),
      b.sequence(TYPE, COLLECTOR)
      ));

    b.rule(COLLECTOR).is(b.firstOf(
      b.sequence(LCOLLECT, COLLECT_STMTS, RCOLLECT),
      b.sequence(LLCOLLECT, COLLECT_STMTS, RRCOLLECT)
      ));

    b.rule(COLLECT_STMTS).is(
      b.optional(COLLECT_STMT),
      b.zeroOrMore(COLLECT_JOIN, COLLECT_STMT)
      );

    b.rule(COLLECT_STMT).is(b.firstOf(
      COLLECT_EXPR,
      b.sequence(LPAREN, COLLECT_STMTS, RBRACE)
      ));

    b.rule(COLLECT_EXPR).is(b.firstOf(
      b.sequence(COLLECTOR_VAL, ISEQUAL, EXPRESSION),
      b.sequence(COLLECTOR_VAL, NOTEQUAL, EXPRESSION)
      ));

    b.rule(COLLECT_JOIN).is(b.firstOf(AND, OR));

    b.rule(COLLECTOR_VAL).is(b.firstOf(VARIABLE, NAME));

    /*
     * Selectors
     */
    b.rule(SELECTOR).is(SELECTLHAND, QMARK, SVALUES);

    b.rule(SVALUES).is(b.firstOf(
      SELECTVAL,
      b.sequence(LBRACE, SINTVALUES, RBRACE)
      )).skip();

    b.rule(SINTVALUES).is(SELECTVAL, b.zeroOrMore(COMMA, SELECTVAL), b.optional(COMMA));

    b.rule(SELECTVAL).is(SELECTLHAND, FARROW, RIGHT_VALUE);

    b.rule(SELECTLHAND).is(b.firstOf(
      FUNCRVALUE,
      NUMBER,
      NAME,
      TYPE,
      QUOTED_TEXT,
      HASH_ARRAY_ACCESS,
      VARIABLE,
      TRUE, FALSE,
      UNDEF,
      DEFAULT,
      REGULAR_EXPRESSION_LITERAL
      ));
  }

  /*
   * Expressions
   * https://docs.puppetlabs.com/puppet/latest/reference/lang_expressions.htmls
   */
  public static void expressions(LexerfulGrammarBuilder b) {

    b.rule(EXPRESSION).is(b.firstOf(
      BOOL_EXPRESSION,
      // RIGHT_VALUE,
      HASH));

    b.rule(EXPRESSIONS).is(EXPRESSION, b.zeroOrMore(COMMA, EXPRESSION)).skip();

    // https://docs.puppetlabs.com/puppet/latest/reference/lang_expressions.html#order-of-operations

    b.rule(UNARY_NOT_EXPRESSION).is(b.optional(NOT), ATOM).skipIfOneChild();
    b.rule(UNARY_NEG_EXPRESSION).is(b.optional(MINUS), UNARY_NOT_EXPRESSION).skipIfOneChild();

    b.rule(IN_EXPRESSION).is(UNARY_NEG_EXPRESSION, b.zeroOrMore(IN, UNARY_NEG_EXPRESSION)).skipIfOneChild();

    b.rule(MATCH_EXPRESSION).is(IN_EXPRESSION, b.zeroOrMore(MATCH_OPERATOR, IN_EXPRESSION)).skipIfOneChild();
    b.rule(MULTIPLICATIVE_EXPRESSION).is(MATCH_EXPRESSION, b.zeroOrMore(M_OPER, MATCH_EXPRESSION)).skipIfOneChild();
    b.rule(ADDITIVE_EXPRESSION).is(MULTIPLICATIVE_EXPRESSION, b.zeroOrMore(A_OPER, MULTIPLICATIVE_EXPRESSION)).skipIfOneChild();
    b.rule(SHIFT_EXPRESSION).is(ADDITIVE_EXPRESSION, b.zeroOrMore(SHIFT_OPER, ADDITIVE_EXPRESSION)).skipIfOneChild();
    b.rule(COMPARISON).is(SHIFT_EXPRESSION, b.zeroOrMore(COMP_OPERATOR, SHIFT_EXPRESSION)).skipIfOneChild();
    b.rule(BOOL_EXPRESSION).is(COMPARISON, b.zeroOrMore(BOOL_OPERATOR, COMPARISON)).skipIfOneChild();

    b.rule(ATOM).is(b.firstOf(
      b.sequence(LPAREN, BOOL_EXPRESSION, RPAREN),
      RIGHT_VALUE,
      REGULAR_EXPRESSION_LITERAL)).skip();

    // <arithop> ::= "+" | "-" | "/" | "*" | "<<" | ">>"
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

    // <boolop> ::= "and" | "or"
    b.rule(BOOL_OPERATOR).is(b.firstOf(
      AND,
      OR));

    // <compop> ::= "==" | "!=" | ">" | ">=" | "<=" | "<"
    b.rule(COMP_OPERATOR).is(b.firstOf(
      ISEQUAL,
      NOTEQUAL,
      GREATERTHAN,
      GREATEREQUAL,
      LESSEQUAL,
      LESSTHAN));

    // <matchop> ::= "=~" | "!~"
    b.rule(MATCH_OPERATOR).is(b.firstOf(
      MATCH,
      NOMATCH));

    b.rule(RIGHT_VALUE).is(b.firstOf(
      QUOTED_TEXT,
      SELECTOR,
      FUNCRVALUE,
      NUMBER,
      NAME,
      TRUE, FALSE,
      HASH_ARRAY_ACCESSES,
      VARIABLE,
      ARRAY,
      RESOURCE_REF,
      TYPE,
      UNDEF)).skip();

    // https://github.com/puppetlabs/puppet-specifications/blob/master/language/lexical_structure.md#numbers
    b.rule(NUMBER).is(b.firstOf(
      HEX_INTEGER,
      OCTAL_INTEGER,
      INTEGER,
      FLOAT
      ));
  }
}
