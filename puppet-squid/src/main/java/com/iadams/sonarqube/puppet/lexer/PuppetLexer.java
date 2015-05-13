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
package com.iadams.sonarqube.puppet.lexer;

import com.iadams.sonarqube.puppet.PuppetConfiguration;
import com.iadams.sonarqube.puppet.api.PuppetKeyword;
import com.iadams.sonarqube.puppet.api.PuppetPunctuator;
import com.iadams.sonarqube.puppet.api.PuppetTokenType;
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.impl.Lexer;
import com.sonar.sslr.impl.channel.BlackHoleChannel;
import com.sonar.sslr.impl.channel.IdentifierAndKeywordChannel;
import com.sonar.sslr.impl.channel.PunctuatorChannel;

import static com.sonar.sslr.impl.channel.RegexpChannelBuilder.commentRegexp;
import static com.sonar.sslr.impl.channel.RegexpChannelBuilder.regexp;

/**
 * Created by iwarapter
 */
public class PuppetLexer {

    public static final String LITERAL = "(?:"
            + "\"([^\"\\\\]*+(\\\\[\\s\\S])?+)*+\""
            + "|'([^'\\\\]*+(\\\\[\\s\\S])?+)*+'"
            + ")";

    private PuppetLexer() {
    }

    public static final String HASH_LINE_COMMENT = "#[^\\n\\r]*+";
    public static final String SLASH_LINE_COMMENT = "//[^\\n\\r].*|#[^\\n\\r].*";
    public static final String MULTI_LINE_COMMENT = "/\\*[\\s\\S]*?\\*/";

    public static final String COMMENT = "(?:" + HASH_LINE_COMMENT + "|" + SLASH_LINE_COMMENT + "|" + MULTI_LINE_COMMENT + ")";

    public static Lexer create(PuppetConfiguration conf) {
    return Lexer.builder()
            .withCharset(conf.getCharset())
            .withFailIfNoChannelToConsumeOneCharacter(true)
            .withChannel(new IdentifierAndKeywordChannel("[a-zA-Z]([a-zA-Z0-9_]*[a-zA-Z0-9])?+", true, PuppetKeyword.values()))
            .withChannel(regexp(PuppetTokenType.FLOAT, "[0-9]+\\.[0-9]+([eE]?[0-9]+)?"))
            .withChannel(regexp(PuppetTokenType.HEX_INTEGER, "0(x|X)[0-9a-fA-F]+"))
            .withChannel(regexp(PuppetTokenType.OCTAL_INTEGER, "0[0-7]+"))
            .withChannel(regexp(PuppetTokenType.INTEGER, "[0-9]+"))
            .withChannel(regexp(PuppetTokenType.VARIABLE, "\\$(::)?((\\w|-)+::)*(\\w|-)+"))

                    // String Literals
            .withChannel(regexp(GenericTokenType.LITERAL, LITERAL))

            .withChannel(commentRegexp(COMMENT))
            .withChannel(new PunctuatorChannel(PuppetPunctuator.values()))
            .withChannel(new BlackHoleChannel("[ \t\r\n]+"))
            .build();
    }
}
