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

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.TokenType;

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
