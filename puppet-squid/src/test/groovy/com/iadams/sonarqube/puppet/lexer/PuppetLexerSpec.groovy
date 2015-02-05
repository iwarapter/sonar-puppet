package com.iadams.sonarqube.puppet.lexer

import com.iadams.sonarqube.puppet.api.PuppetKeyword
import com.iadams.sonarqube.puppet.api.PuppetPunctuator
import com.sonar.sslr.impl.Lexer
import spock.lang.Specification

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

    def "Keywords lexed correctly"() {
        given:
        lexer.lex(input)

        expect:
        hasToken(input, token)

        where:
        input       | token
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

    def "Punctuators lexed correctly"() {
        given:
        lexer.lex(input)

        expect:
        hasToken(input, token)

        where:
        input   | token
        '=='    | PuppetPunctuator.EQU
        '!='    | PuppetPunctuator.NOT_EQU
        '<'     | PuppetPunctuator.LT
        '>'     | PuppetPunctuator.GT
        '<='    | PuppetPunctuator.LT_EQU
        '>='    | PuppetPunctuator.GT_EQU
        '=~'    | PuppetPunctuator.REG_MAT
        '!~'    | PuppetPunctuator.REG_NMAT
        'in'    | PuppetPunctuator.IN
        'and'   | PuppetPunctuator.AND
        'or'    | PuppetPunctuator.OR
        '!'     | PuppetPunctuator.NOT
        '+'     | PuppetPunctuator.PLUS
        '-'     | PuppetPunctuator.MINUS
        '/'     | PuppetPunctuator.DIV
        '*'     | PuppetPunctuator.MUL
        '%'     | PuppetPunctuator.MOD
        '<<'    | PuppetPunctuator.L_SHIFT
        '>>'    | PuppetPunctuator.R_SHIFT
        '`'     | PuppetPunctuator.BACKTICK
        ' '     | PuppetPunctuator.LPARENTHESIS
        ')'     | PuppetPunctuator.RPARENTHESIS
        '['     | PuppetPunctuator.LBRACKET
        ']'     | PuppetPunctuator.RBRACKET
        '{'     | PuppetPunctuator.LCURLYBRACE
        '}'     | PuppetPunctuator.RCURLYBRACE
        ','     | PuppetPunctuator.COMMA
        ':'     | PuppetPunctuator.COLON
        '.'     | PuppetPunctuator.DOT
        ';'     | PuppetPunctuator.SEMICOLON
        '@'     | PuppetPunctuator.AT
        '='     | PuppetPunctuator.ASSIGN
        '+='    | PuppetPunctuator.PLUS_ASSIGN
        '-='    | PuppetPunctuator.MINUS_ASSIGN
        '*='    | PuppetPunctuator.MUL_ASSIGN
        '/='    | PuppetPunctuator.DIV_ASSIGN
        '//='   | PuppetPunctuator.DIV_DIV_ASSIGN
        '%='    | PuppetPunctuator.MOD_ASSIGN
        '&='    | PuppetPunctuator.AND_ASSIGN
        '|='    | PuppetPunctuator.OR_ASSIGN
        '^='    | PuppetPunctuator.XOR_ASSIGN
        '>>='   | PuppetPunctuator.RIGHT_ASSIGN
        '<<='   | PuppetPunctuator.LEFT_ASSIGN
        '**='   | PuppetPunctuator.MUL_MUL_ASSIGN
    }

    def "comments lexed correctly"() {
        expect:
        assertThat(lexer.lex("/*test*/"), hasComment("/*test*/"));
        assertThat(lexer.lex("/*test*/*/"), hasComment("/*test*/"));
        assertThat(lexer.lex("/*test/* /**/"), hasComment("/*test/* /**/"));
        assertThat(lexer.lex("/*test1\ntest2\ntest3*/"), hasComment("/*test1\ntest2\ntest3*/"));
    }
}
