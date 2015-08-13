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
package com.iadams.sonarqube.puppet.lexer

import com.google.common.base.Charsets
import com.iadams.sonarqube.puppet.PuppetConfiguration
import com.sonar.sslr.api.Token
import com.sonar.sslr.api.TokenType
import com.sonar.sslr.impl.Lexer
import spock.lang.Specification
import spock.lang.Unroll

import static com.iadams.sonarqube.puppet.api.PuppetKeyword.*
import static com.iadams.sonarqube.puppet.api.PuppetPunctuator.*
import static com.iadams.sonarqube.puppet.api.PuppetTokenType.*
import static com.iadams.sonarqube.puppet.api.PuppetTokenType.INTEGER
import static com.iadams.sonarqube.puppet.api.PuppetTokenType.VARIABLE;
import static com.iadams.sonarqube.puppet.api.PuppetTokenType.REGULAR_EXPRESSION_LITERAL;
import static com.sonar.sslr.test.lexer.LexerMatchers.hasComment;
import static com.sonar.sslr.test.lexer.LexerMatchers.hasToken;
import static org.junit.Assert.assertThat;

class PuppetLexerSpec extends Specification {

  private static Lexer lexer;

  def setupSpec() {
    lexer = PuppetLexer.create(new PuppetConfiguration(Charsets.UTF_8));
  }

  def "names are lexed correctly"() {
    expect:
    assertThat(lexer.lex('apache::port'), hasToken('apache::port', NAME))
    assertThat(lexer.lex('::apache'), hasToken('::apache', NAME))
    assertThat(lexer.lex('::apache::port'), hasToken('::apache::port', NAME))
    //3x pattern only
    assertThat(lexer.lex('contains-dash'), hasToken('contains-dash', NAME))
  }

  def "refs are lexed correctly"() {
    expect:
    assertThat(lexer.lex('File'), hasToken('File', REF))
    assertThat(lexer.lex('::File'), hasToken('::File', REF))
    assertThat(lexer.lex('Class'), hasToken('Class', REF))
    assertThat(lexer.lex('Integer'), hasToken('Integer', REF))
  }

  def "variables are lexed correctly"() {
    expect:
    assertThat(lexer.lex('$apache::port'), hasToken('$apache::port', VARIABLE))
    assertThat(lexer.lex('$::apache'), hasToken('$::apache', VARIABLE))
    assertThat(lexer.lex('$::apache::port'), hasToken('$::apache::port', VARIABLE))
  }

  @Unroll
  def "Keyword #input lexed correctly"() {
    given:
    lexer.lex(input)

    expect:
    containsToken(input, token)

    where:
    input      | token
    'and'      | AND
    'case'     | CASE
    'class'    | CLASS
    'default'  | DEFAULT
    'define'   | DEFINE
    'else'     | ELSE
    'elsif'    | ELSIF
    'false'    | FALSE
    'if'       | IF
    'in'       | IN
    'import'   | IMPORT
    'inherits' | INHERITS
    'node'     | NODE
    'or'       | OR
    'true'     | TRUE
    'undef'    | UNDEF
    'unless'   | UNLESS
  }

  @Unroll
  def "Punctuator #token lexed correctly"() {
    given:
    lexer.lex(input)

    expect:
    containsToken(input, token)

    where:
    input | token
    "/"   | DIV
    "*"   | MUL
    "["   | LBRACK
    "]"   | RBRACK
    "{"   | LBRACE
    "}"   | RBRACE
    "("   | LPAREN
    ")"   | RPAREN
    "=="  | ISEQUAL
    "=~"  | MATCH
    "=>"  | FARROW
    "="   | EQUALS
    "+="  | APPENDS
    "+>"  | PARROW
    "+"   | PLUS
    ">="  | GREATEREQUAL
    ">>"  | RSHIFT
    ">"   | GREATERTHAN
    "<="  | LESSEQUAL
    "<<|" | LLCOLLECT
    "<-"  | OUT_EDGE
    "<~"  | OUT_EDGE_SUB
    "<|"  | LCOLLECT
    "<<"  | LSHIFT
    "<"   | LESSTHAN
    "!~"  | NOMATCH
    "!="  | NOTEQUAL
    "!"   | NOT
    "|>>" | RRCOLLECT
    "|>"  | RCOLLECT
    "->"  | IN_EDGE
    "~>"  | IN_EDGE_SUB
    "-"   | MINUS
    ","   | COMMA
    "."   | DOT
    ":"   | COLON
    "@"   | AT
    ";"   | SEMIC
    "?"   | QMARK
    "\\"  | BACKSLASH
    "%"   | MODULO
    "|"   | PIPE
  }

  def "comments lexed correctly"() {
    expect:
    assertThat(lexer.lex("/*test*/"), hasComment("/*test*/"));
    assertThat(lexer.lex("/*test*/*/"), hasComment("/*test*/"));
    assertThat(lexer.lex("/*test/* /**/"), hasComment("/*test/* /**/"));
    assertThat(lexer.lex("/*test1\ntest2\ntest3*/"), hasComment("/*test1\ntest2\ntest3*/"));
  }

  def "expressions lex correctly"() {
    given:
    lexer.lex("1 + 1")

    expect:
    containsToken('1', INTEGER)
    containsToken('+', PLUS)
    containsToken('1', INTEGER)
  }

  @Unroll
  def "#input is a #token"() {
    given:
    lexer.lex(input)

    expect:
    containsToken(input, token)

    where:
    input   | token
    '0777'  | OCTAL_INTEGER
    '0x777' | HEX_INTEGER
    '0xdef' | HEX_INTEGER
    '0Xdef' | HEX_INTEGER
    '0xDEF' | HEX_INTEGER
    '0'     | INTEGER
    '123123'| INTEGER
    '0.3'   | FLOAT
    '1.3'   | FLOAT
    '12.124'| FLOAT
    '3e5'		| FLOAT
    '1.2'		| FLOAT
    '5235.32'	| FLOAT
    '6.667e-11'	| FLOAT
    '3.3'		| FLOAT
    '5E6'		| FLOAT
    '7E-3'	| FLOAT
    '5.333E2'| FLOAT
    '1e2'		| FLOAT
    '1.1'		| FLOAT
  }

  def "example file is lexed correctly"() {
    given:
    String codeChunksResource = "/metrics/lines_of_code.pp"
    String codeChunksPathName = getClass().getResource(codeChunksResource).getPath()
    String content = new File(codeChunksPathName).text

    lexer.lex(content)

    expect:
    containsToken('$variable', VARIABLE)
    containsToken('"this is a string"', DOUBLE_QUOTED_STRING_LITERAL)
    containsToken('user', NAME)
    containsToken('{', LBRACE)
    containsToken("'katie'", SINGLE_QUOTED_STRING_LITERAL)
    containsToken(':', COLON)
  }

  def "function calls are lexed correctly"() {
    given:
    lexer.lex('str2bool($is_virtual)')

    expect:
    containsToken('str2bool', NAME)
    containsToken('(', LPAREN)
    containsToken('$is_virtual', VARIABLE)
    containsToken(')', RPAREN)
  }

  def "assignments lexed correctly"() {
    given:
    lexer.lex('$var = 10')

    expect:
    containsToken('$var', VARIABLE)
    containsToken('=', EQUALS)
    containsToken('10', INTEGER)
  }

  def "double quoted literal"() {
    given:
    lexer.lex('$var = "string"')

    expect:
    containsToken('$var', VARIABLE)
    containsToken('=', EQUALS)
    containsToken('"string"', DOUBLE_QUOTED_STRING_LITERAL)
  }

  def "fully qualified names"() {
    given:
    lexer.lex('include role::solaris')

    expect:
    containsToken('include', NAME)
    containsToken('role::solaris', NAME)
  }

  def "virtual resources lex correctly"() {
    given:
    lexer.lex("@user {'deploy':")

    expect:
    containsToken('@', AT)
    containsToken('user', NAME)
    containsToken('{', LBRACE)
    containsToken("'deploy'", SINGLE_QUOTED_STRING_LITERAL)
    containsToken(':', COLON)
  }

  def "node with regex lex correctly"(){
    given:
    lexer.lex("node /^www\\d+\$/ {}")

    expect:
    containsToken('node', NODE)
    containsToken('/^www\\d+$/', REGULAR_EXPRESSION_LITERAL)
    containsToken('{', LBRACE)
    containsToken('}', RBRACE)
  }

  @Unroll
  def "matches regular expressions"() {
    expect:
    assertRegexp(statement);

    where:
    statement << ["/^www\\d+\$/",
                  "/^(foo|bar)\\.example\\.com\$/",
                  "/^(Debian|Ubuntu)\$/",
                  "/^dev-[^\\s]*\$/",
                  "/^(foo|bar)\\.example\\.com\$/",
                  "/\\//"]
  }

  def "strings with regex lex correctly"(){
    given:
    lexer.lex('/(?i-mx:ubuntu|debian)/  => "apa/che2"')

    expect:
    containsToken('/(?i-mx:ubuntu|debian)/', REGULAR_EXPRESSION_LITERAL)
    containsToken('=>', FARROW)
    containsToken('"apa/che2"', DOUBLE_QUOTED_STRING_LITERAL)
  }

  private static void assertRegexp(String regexp) {
    assertThat(lexer.lex(regexp), hasToken(regexp, REGULAR_EXPRESSION_LITERAL));
  }

  private boolean containsToken(String value, TokenType type) {
    for (Token token : lexer.tokens) {
      if (token.getValue().equals(value) && token.getType() == type) {
        return true;
      }
    }
    return false;
  }
}
