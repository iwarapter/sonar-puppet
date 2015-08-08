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

import org.sonar.sslr.channel.Channel;
import org.sonar.sslr.channel.CodeReader;

import com.google.common.collect.ImmutableMap;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.Lexer;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.iadams.sonarqube.puppet.api.PuppetTokenType.NAME;

/**
 * Because all the puppet grammar references the {@code NAME} token instead of {@code IDENTIFIER}
 * i have implemented an almost identical version of the {@link com.sonar.sslr.impl.channel.IdentifierAndKeywordChannel}
 * to avoid any confusion when implementing the grammar from the puppet docs.
 */
public class NameAndKeywordChannel extends Channel<Lexer> {

  private final Map<String, TokenType> keywordsMap;
  private final StringBuilder tmpBuilder = new StringBuilder();
  private final Matcher matcher;
  private final boolean caseSensitive;
  private final Token.Builder tokenBuilder = Token.builder();

  /**
   * @throws java.util.regex.PatternSyntaxException if the expression's syntax is invalid
   */
  public NameAndKeywordChannel(String regexp, boolean caseSensitive, TokenType[]... keywordSets) {
    ImmutableMap.Builder<String, TokenType> keywordsMapBuilder = ImmutableMap.builder();
    for (TokenType[] keywords : keywordSets) {
      for (TokenType keyword : keywords) {
        String keywordValue = caseSensitive ? keyword.getValue() : keyword.getValue().toUpperCase();
        keywordsMapBuilder.put(keywordValue, keyword);
      }
    }
    this.keywordsMap = keywordsMapBuilder.build();
    this.caseSensitive = caseSensitive;
    matcher = Pattern.compile(regexp).matcher("");
  }

  @Override
  public boolean consume(CodeReader code, Lexer lexer) {
    if (code.popTo(matcher, tmpBuilder) > 0) {
      String word = tmpBuilder.toString();
      String wordOriginal = word;
      if (!caseSensitive) {
        word = word.toUpperCase();
      }

      TokenType keywordType = keywordsMap.get(word);
      Token token = tokenBuilder
        .setType(keywordType == null ? NAME : keywordType)
        .setValueAndOriginalValue(word, wordOriginal)
        .setURI(lexer.getURI())
        .setLine(code.getPreviousCursor().getLine())
        .setColumn(code.getPreviousCursor().getColumn())
        .build();

      lexer.addToken(token);

      tmpBuilder.delete(0, tmpBuilder.length());
      return true;
    }
    return false;
  }

}
