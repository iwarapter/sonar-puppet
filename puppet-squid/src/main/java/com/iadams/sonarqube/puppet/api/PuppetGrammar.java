package com.iadams.sonarqube.puppet.api;

import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.grammar.LexerfulGrammarBuilder;

import static com.iadams.sonarqube.puppet.api.PuppetTokenType.DEDENT;
import static com.iadams.sonarqube.puppet.api.PuppetTokenType.INDENT;
import static com.iadams.sonarqube.puppet.api.PuppetTokenType.NEWLINE;
import static com.sonar.sslr.api.GenericTokenType.EOF;
import static com.sonar.sslr.api.GenericTokenType.IDENTIFIER;

/**
 * Created by iwarapter
 */
public enum PuppetGrammar  implements GrammarRuleKey {

    TEST,

    //EXPRESSIONS
    COMP_OPERATOR,

    //SIMPLE STATEMENTS

    //CONDITIONAL STATEMENTS
    CONDITIONAL_STMT,
    IF_STMT,
    UNLESS_STMT,

    FILE_INPUT;

    public static LexerfulGrammarBuilder create() {
        LexerfulGrammarBuilder b = LexerfulGrammarBuilder.create();

        //b.rule(FILE_INPUT).is(b.zeroOrMore(b.firstOf(NEWLINE, STATEMENT)), EOF);

        grammar(b);
        conditionalStatements(b);
        //simpleStatements(b);
        //expressions(b);

        b.setRootRule(FILE_INPUT);
        b.buildWithMemoizationOfMatchesForAllRules();

        return b;
    }

    public static void grammar(LexerfulGrammarBuilder b) {

    }

    /**
     * Conditional Statements
     * https://docs.puppetlabs.com/puppet/3.7/reference/lang_conditional.html
     * @param b
     */
    public static void conditionalStatements(LexerfulGrammarBuilder b){

        //b.rule(CONDITIONAL_STMT).is(b.firstOf(
        //        IF_STMT,
        //        UNLESS_STMT));
        //b.rule(IF_STMT).is("if", TEST, ":", SUITE, b.zeroOrMore("elif", TEST, ":", SUITE), b.optional("else", ":", SUITE));
    }

    /**
     * Expressions
     * https://docs.puppetlabs.com/puppet/latest/reference/lang_expressions.html
     * @param b
     */
    public static void expressions(LexerfulGrammarBuilder b){

        //<compop>  ::= "==" | "!=" | ">" | ">=" | "<=" | "<"
        b.rule(COMP_OPERATOR).is(b.firstOf(
                "==",
                "!=",
                ">",
                ">=",
                "<=",
                "<"));
    }
}
