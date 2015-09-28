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
package com.iadams.sonarqube.puppet.highlighter;

import com.google.common.annotations.VisibleForTesting;
import com.iadams.sonarqube.puppet.PuppetConfiguration;
import com.iadams.sonarqube.puppet.api.PuppetKeyword;
import com.iadams.sonarqube.puppet.api.PuppetTokenType;
import com.iadams.sonarqube.puppet.lexer.PuppetLexer;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.api.Trivia;
import com.sonar.sslr.impl.Lexer;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

import org.sonar.api.source.Highlightable;

public class PuppetHighlighter {

  private Lexer lexer;
  private Charset charset;

  public PuppetHighlighter(PuppetConfiguration conf) {
    this.lexer = PuppetLexer.create(conf);
    this.charset = conf.getCharset();
  }

  public void highlight(Highlightable highlightable, File file) {
    SourceFileOffsets offsets = new SourceFileOffsets(file, charset);
    List<Token> tokens = lexer.lex(file);
    doHighlight(highlightable, tokens, offsets);
  }

  @VisibleForTesting
  public void highlight(Highlightable highlightable, String string) {
    SourceFileOffsets offsets = new SourceFileOffsets(string);
    List<Token> tokens = lexer.lex(string);
    doHighlight(highlightable, tokens, offsets);
  }

  private void doHighlight(Highlightable highlightable, List<Token> tokens, SourceFileOffsets offsets) {
    Highlightable.HighlightingBuilder highlighting = highlightable.newHighlighting();
    highlightStringsAndVariablesAndKeywords(highlighting, tokens, offsets);
    highlightComments(highlighting, tokens, offsets);
    highlighting.done();
  }

  private static void highlightComments(Highlightable.HighlightingBuilder highlighting, List<Token> tokens, SourceFileOffsets offsets) {
    for (Token token : tokens) {
      if (!token.getTrivia().isEmpty()) {
        for (Trivia trivia : token.getTrivia()) {
          highlight(highlighting, offsets.startOffset(trivia.getToken()), offsets.endOffset(trivia.getToken()), "cd");
        }
      }
    }
  }

  private void highlightStringsAndVariablesAndKeywords(Highlightable.HighlightingBuilder highlighting, List<Token> tokens, SourceFileOffsets offsets) {
    for (Token token : tokens) {
      if (PuppetTokenType.SINGLE_QUOTED_STRING_LITERAL.equals(token.getType())
        || PuppetTokenType.DOUBLE_QUOTED_STRING_LITERAL.equals(token.getType())) {
        highlight(highlighting, offsets.startOffset(token), offsets.endOffset(token), "p");
      } else if (isKeyword(token.getType())) {
        highlight(highlighting, offsets.startOffset(token), offsets.endOffset(token), "k");
      } else if (PuppetTokenType.VARIABLE.equals(token.getType())) {
        highlight(highlighting, offsets.startOffset(token), offsets.endOffset(token), "a");
      }
    }
  }

  private static void highlight(Highlightable.HighlightingBuilder highlighting, int startOffset, int endOffset, String code) {
    if (endOffset > startOffset) {
      highlighting.highlight(startOffset, endOffset, code);
    }
  }

  public boolean isKeyword(TokenType type) {
    for (TokenType keywordType : PuppetKeyword.values()) {
      if (keywordType.equals(type)) {
        return true;
      }
    }
    return false;
  }
}
