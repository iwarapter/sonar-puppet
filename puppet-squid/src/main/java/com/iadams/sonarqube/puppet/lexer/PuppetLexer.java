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
            .withChannel(regexp(PuppetTokenType.HEX_INTEGER, "0(x|X)[0-9a-fA-F]+"))
            .withChannel(regexp(PuppetTokenType.OCTAL_INTEGER, "0[0-7]+"))
            .withChannel(regexp(PuppetTokenType.INTEGER, "[0-9]+"))
            .withChannel(regexp(PuppetTokenType.VARIABLE, "\\$(::)?(\\w+::)*\\w+"))

                    // String Literals
            .withChannel(regexp(GenericTokenType.LITERAL, LITERAL))

            .withChannel(commentRegexp(COMMENT))
            .withChannel(new PunctuatorChannel(PuppetPunctuator.values()))
            .withChannel(new BlackHoleChannel("[ \t\r\n]+"))
            .build();
    }
}
