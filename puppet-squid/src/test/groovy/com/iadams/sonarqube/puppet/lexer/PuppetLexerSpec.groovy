package com.iadams.sonarqube.puppet.lexer

import com.iadams.sonarqube.puppet.api.PuppetKeyword
import com.iadams.sonarqube.puppet.api.PuppetPunctuator
import com.sonar.sslr.api.Token
import com.sonar.sslr.api.TokenType
import com.sonar.sslr.impl.Lexer
import spock.lang.Specification
import spock.lang.Unroll

import static com.sonar.sslr.api.GenericTokenType.IDENTIFIER;
import static com.sonar.sslr.test.lexer.LexerMatchers.hasComment;
import static com.sonar.sslr.test.lexer.LexerMatchers.hasToken;
import static org.junit.Assert.assertThat;

/**
 * Created by iwarapter
 */
class PuppetLexerSpec extends Specification {

    Lexer lexer = PuppetLexer.create();

    def "lex Identifiers"() {
        assertThat(lexer.lex("abc"), hasToken("abc", IDENTIFIER));
        assertThat(lexer.lex("abc0"), hasToken("abc0", IDENTIFIER));
        assertThat(lexer.lex("abc_0"), hasToken("abc_0", IDENTIFIER));
        assertThat(lexer.lex("i"), hasToken("i", IDENTIFIER));
    }

    @Unroll
    def "Keyword #input lexed correctly"() {
        given:
        lexer.lex(input)

        expect:
        containsToken(input, token)

        where:
        input       | token
        'and'       | PuppetKeyword.AND
        'or'        | PuppetKeyword.OR
        'in'        | PuppetKeyword.IN
        'before'    | PuppetKeyword.BEFORE
        'case'      | PuppetKeyword.CASE
        'class'     | PuppetKeyword.CLASS
        'default'   | PuppetKeyword.DEFAULT
        'define'    | PuppetKeyword.DEFINE
        'else'      | PuppetKeyword.ELSE
        'elsif'     | PuppetKeyword.ELSIF
        'false'     | PuppetKeyword.FALSE
        'if'        | PuppetKeyword.IF
        'import'    | PuppetKeyword.IMPORT
        'inherits'  | PuppetKeyword.INHERITS
        'node'      | PuppetKeyword.NODE
        'notify'    | PuppetKeyword.NOTIFY
        'require'   | PuppetKeyword.REQUIRE
        'subscribe' | PuppetKeyword.SUBSCRIBE
        'true'      | PuppetKeyword.TRUE
        'undef'     | PuppetKeyword.UNDEF
        'unless'    | PuppetKeyword.UNLESS
    }

    @Unroll
    def "Punctuator #token lexed correctly"() {
        given:
        lexer.lex(input)

        expect:
        containsToken(input, token)

        where:
        input   | token
        "/"     | PuppetPunctuator.DIV
        "*"     | PuppetPunctuator.MUL
        "["     | PuppetPunctuator.LBRACK
        "]"     | PuppetPunctuator.RBRACK
        "{"     | PuppetPunctuator.LBRACE
        "}"     | PuppetPunctuator.RBRACE
        "("     | PuppetPunctuator.LPAREN
        ")"     | PuppetPunctuator.RPAREN
        "=="    | PuppetPunctuator.ISEQUAL
        "=~"    | PuppetPunctuator.MATCH
        "=>"    | PuppetPunctuator.FARROW
        "="     | PuppetPunctuator.EQUALS
        "+="    | PuppetPunctuator.APPENDS
        "+>"    | PuppetPunctuator.PARROW
        "+"     | PuppetPunctuator.PLUS
        ">="    | PuppetPunctuator.GREATEREQUAL
        ">>"    | PuppetPunctuator.RSHIFT
        ">"     | PuppetPunctuator.GREATERTHAN
        "<="    | PuppetPunctuator.LESSEQUAL
        "<<|"   | PuppetPunctuator.LLCOLLECT
        "<-"    | PuppetPunctuator.OUT_EDGE
        "<~"    | PuppetPunctuator.OUT_EDGE_SUB
        "<|"    | PuppetPunctuator.LCOLLECT
        "<<"    | PuppetPunctuator.LSHIFT
        "<"     | PuppetPunctuator.LESSTHAN
        "!~"    | PuppetPunctuator.NOMATCH
        "!="    | PuppetPunctuator.NOTEQUAL
        "!"     | PuppetPunctuator.NOT
        "|>>"   | PuppetPunctuator.RRCOLLECT
        "|>"    | PuppetPunctuator.RCOLLECT
        "->"    | PuppetPunctuator.IN_EDGE
        "~>"    | PuppetPunctuator.IN_EDGE_SUB
        "-"     | PuppetPunctuator.MINUS
        ","     | PuppetPunctuator.COMMA
        "."     | PuppetPunctuator.DOT
        ":"     | PuppetPunctuator.COLON
        "@"     | PuppetPunctuator.AT
        ";"     | PuppetPunctuator.SEMIC
        "?"     | PuppetPunctuator.QMARK
        "\\"    | PuppetPunctuator.BACKSLASH
        "%"     | PuppetPunctuator.MODULO
        "|"     | PuppetPunctuator.PIPE
    }

    def "comments lexed correctly"() {
        expect:
        assertThat(lexer.lex("/*test*/"), hasComment("/*test*/"));
        assertThat(lexer.lex("/*test*/*/"), hasComment("/*test*/"));
        assertThat(lexer.lex("/*test/* /**/"), hasComment("/*test/* /**/"));
        assertThat(lexer.lex("/*test1\ntest2\ntest3*/"), hasComment("/*test1\ntest2\ntest3*/"));
    }

    def "example file is lexed correctly"(){
        given:
        String codeChunksResource = "/metrics/lines_of_code.pp"
        String codeChunksPathName = getClass().getResource(codeChunksResource).getPath()
        String content = new File(codeChunksPathName).text

        lexer.lex(content)

        expect:
        containsToken('user',IDENTIFIER)
    }

    private boolean containsToken(String value, TokenType type){
        for (Token token : lexer.tokens) {
            if (token.getValue().equals(value) && token.getType() == type) {
                return true;
            }
        }
        return false;
    }
}
