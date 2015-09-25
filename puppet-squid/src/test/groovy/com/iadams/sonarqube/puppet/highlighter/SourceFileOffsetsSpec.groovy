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
package com.iadams.sonarqube.puppet.highlighter

import com.google.common.base.Charsets
import com.iadams.sonarqube.puppet.PuppetConfiguration
import com.iadams.sonarqube.puppet.lexer.PuppetLexer
import com.sonar.sslr.api.Token
import com.sonar.sslr.impl.Lexer
import spock.lang.Specification

class SourceFileOffsetsSpec extends Specification {

  private Lexer lexer = PuppetLexer.create(new PuppetConfiguration(Charsets.UTF_8));

  def "one line"() {
    given:
    String string = "class abc {}";
    SourceFileOffsets offsets = new SourceFileOffsets(string);
    List<Token> tokens = lexer.lex(string);

    expect:
    offsets.startOffset(tokens.get(0)) == 0;
    offsets.endOffset(tokens.get(0)) == 5;

    offsets.startOffset(tokens.get(1)) == 6;
    offsets.endOffset(tokens.get(1)) == 9;

    offsets.startOffset(tokens.get(2)) == 10;
    offsets.endOffset(tokens.get(2)) == 11;

    offsets.startOffset(tokens.get(3)) == 11;
    offsets.endOffset(tokens.get(3)) == 12;
  }

  def "three lines"() {
    given:
    String string = "class abc {\n\$abc => 'abc'\n }";
    SourceFileOffsets offsets = new SourceFileOffsets(string);
    List<Token> tokens = lexer.lex(string);

    expect:
    offsets.startOffset(tokens.get(0)) == 0;
    offsets.endOffset(tokens.get(0)) == 5;

    offsets.startOffset(tokens.get(1)) == 6;
    offsets.endOffset(tokens.get(1)) == 9;

    offsets.startOffset(tokens.get(2)) == 10;
    offsets.endOffset(tokens.get(2)) == 11;

    offsets.startOffset(tokens.get(3)) == 12;
    offsets.endOffset(tokens.get(3)) == 16;

    offsets.startOffset(tokens.get(4)) == 17;
    offsets.endOffset(tokens.get(4)) == 19;

    offsets.startOffset(tokens.get(5)) == 20;
    offsets.endOffset(tokens.get(5)) == 25;

    offsets.startOffset(tokens.get(6)) == 27;
    offsets.endOffset(tokens.get(6)) == 28;
  }

}
