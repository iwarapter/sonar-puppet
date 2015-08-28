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
package com.iadams.sonarqube.puppet.lexer;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import com.iadams.sonarqube.puppet.api.PuppetKeyword;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.Lexer;

import java.util.List;
import java.util.Set;

import org.sonar.sslr.channel.Channel;
import org.sonar.sslr.channel.CodeReader;

import static com.iadams.sonarqube.puppet.api.PuppetTokenType.REGULAR_EXPRESSION_LITERAL;
import static com.sonar.sslr.impl.channel.RegexpChannelBuilder.regexp;

public class PuppetRegexpChannel extends Channel<Lexer> {

  public static final String REGULAR_EXPRESSION = "/([^/]|(?<=\\\\)/)*/";

  private final Channel<Lexer> delegate;

  private static final Set<String> WHOLE_TOKENS = ImmutableSet.of(
    // Binary operators which cannot be followed by a division operator:
    // Match + but not ++. += is handled below.
    "+",
    // Match - but not --. -= is handled below.
    "-",
    // Match . but not a number with a trailing decimal.
    ".",
    // Match /, but not a regexp. /= is handled below
    "/",
    // Second binary operand cannot start a division.
    ",",
    // Ditto binary operand.
    "*");

  private static final String[] ENDS = new String[] {
    // ! prefix operator operand cannot start with a division
    "!",
    // % second binary operand cannot start with a division
    "%",
    // &, && ditto binary operand
    "&",
    // ( expression cannot start with a division
    "(",
    // : property value, labelled statement, and operand of ?: cannot start with a division
    ":",
    // ; statement & for condition cannot start with division
    ";",
    // <, <<, << ditto binary operand
    "<",
    // !=, !==, %=, &&=, &=, *=, +=, -=, /=, <<=, <=, =, ==, ===, >=, >>=, >>>=, ^=, |=, ||=
    // All are binary operands (assignment ops or comparisons) whose right
    // operand cannot start with a division operator
    "=",
    // >, >>, >>> ditto binary operand
    ">",
    // ? expression in ?: cannot start with a division operator
    "?",
    // [ first array value & key expression cannot start with a division
    "[",
    // ^ ditto binary operand
    "^",
    // { statement in block and object property key cannot start with a division
    "{",
    // |, || ditto binary operand
    "|",
    // } PROBLEMATIC: could be an object literal divided or a block.
    // More likely to be start of a statement after a block which cannot start with a /.
    "}",
    // ~ ditto binary operand
    "~"
  };

  public PuppetRegexpChannel() {
    this.delegate = regexp(REGULAR_EXPRESSION_LITERAL, REGULAR_EXPRESSION);
  }

  @Override
  public boolean consume(CodeReader code, Lexer output) {
    if (code.peek() == '/') {
      Token lastToken = getLastToken(output);
      if (lastToken == null || lastToken.getType().equals(PuppetKeyword.NODE) || guessNextIsRegexp(lastToken.getValue())) {
        return delegate.consume(code, output);
      }
    }
    return false;
  }

  private static Token getLastToken(Lexer output) {
    List<Token> tokens = output.getTokens();
    return tokens.isEmpty() ? null : tokens.get(tokens.size() - 1);
  }

  @VisibleForTesting
  static boolean guessNextIsRegexp(String preceder) {
    if (WHOLE_TOKENS.contains(preceder)) {
      return true;
    }
    for (String end : ENDS) {
      if (preceder.endsWith(end)) {
        return true;
      }
    }
    return false;
  }

}
