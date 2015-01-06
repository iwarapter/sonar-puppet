package com.iadams.sonarqube.puppet.colorizer

import org.sonar.colorizer.CodeColorizer
import spock.lang.Specification

/**
 * Created by iwarapter
 */
class PuppetColorizerSpec extends Specification {

    PuppetColorizer puppetColorizer
    CodeColorizer codeColorizer

    def setup(){
        puppetColorizer = new PuppetColorizer()
        codeColorizer = new CodeColorizer(puppetColorizer.getTokenizers())
    }

    def "keywords are coloured"(){
        expect:
        colorize("false").contains('<span class="k">false</span>')
    }

    def "# comment should colorize"(){
        expect:
        colorize("# comment \n new line").contains("<span class=\"cd\"># comment </span>")
    }

    def "should colorize short string literals"(){
        expect:
        colorize('string"').contains("<span class=\"s\">\"string\"</span>")
    }

    def "should colorize long string literals"(){
        expect:
        colorize('"string"').contains('<span class="s">"string"</span>')
    }

    private String colorize(String sourceCode) {
        return codeColorizer.toHtml(new StringReader(sourceCode))
    }
}