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

import com.iadams.sonarqube.puppet.PuppetConfiguration;
import com.iadams.sonarqube.puppet.api.PuppetKeyword;
import com.iadams.sonarqube.puppet.api.PuppetPunctuator;
import com.iadams.sonarqube.puppet.api.PuppetTokenType;
import com.sonar.sslr.impl.Lexer;
import com.sonar.sslr.impl.channel.BlackHoleChannel;
import com.sonar.sslr.impl.channel.PunctuatorChannel;

import static com.sonar.sslr.impl.channel.RegexpChannelBuilder.commentRegexp;
import static com.sonar.sslr.impl.channel.RegexpChannelBuilder.regexp;

public class PuppetLexer {

  public static final String DOUBLE_QUOTED_LITERAL = "\"([^\"\\\\]*+(\\\\[\\s\\S])?+)*+\"";

  public static final String SINGLE_QUOTED_LITERAL = "'([^'\\\\]*+(\\\\[\\s\\S])?+)*+'";

  private PuppetLexer() {
  }

  public static final String HASH_LINE_COMMENT = "#[^\\n\\r]*+";
  public static final String SLASH_LINE_COMMENT = "//[^\\n\\r]*+";
  public static final String MULTI_LINE_COMMENT = "/\\*[\\s\\S]*?\\*/";

  public static final String COMMENT = "(?:" + HASH_LINE_COMMENT + "|" + SLASH_LINE_COMMENT + "|" + MULTI_LINE_COMMENT + ")";

  public static Lexer create(PuppetConfiguration conf) {
    return Lexer.builder()
      .withCharset(conf.getCharset())
      .withFailIfNoChannelToConsumeOneCharacter(true)

      .withChannel(regexp(PuppetTokenType.HEX_INTEGER, "0(x|X)[0-9a-fA-F]+"))
      .withChannel(regexp(PuppetTokenType.OCTAL_INTEGER, "0[0-7]+"))
      .withChannel(regexp(PuppetTokenType.INTEGER, "[1-9][0-9]*"))
      .withChannel(regexp(PuppetTokenType.FLOAT, "0?\\d+(\\.\\d+)?([eE]-?\\d+)?"))

      .withChannel(new NameAndKeywordChannel("((::)?[a-z0-9][-\\w]*)(::[a-z0-9][-\\w]*)*", true, PuppetKeyword.values()))

      .withChannel(regexp(PuppetTokenType.REF, "(::)?[A-Z]\\w*(::[A-Z]\\w*)*"))
      .withChannel(regexp(PuppetTokenType.VARIABLE, "\\$(::)?(\\w+::)*\\w+"))

      // String Literals
      .withChannel(regexp(PuppetTokenType.SINGLE_QUOTED_STRING_LITERAL, SINGLE_QUOTED_LITERAL))
      .withChannel(regexp(PuppetTokenType.DOUBLE_QUOTED_STRING_LITERAL, DOUBLE_QUOTED_LITERAL))

      .withChannel(commentRegexp(COMMENT))

        // Lets play with matching regex!
      .withChannel(new PuppetRegexpChannel())

      .withChannel(new PunctuatorChannel(PuppetPunctuator.values()))
      .withChannel(new BlackHoleChannel("[ \t\r\n]+"))
      .build();
  }
}
