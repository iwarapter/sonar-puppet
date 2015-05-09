/*
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

    /*def "keywords are coloured"(){
        expect:
        colorize("false").contains('<span class="k">false</span>')
    }*/

    def "# comment should colorize"(){
        expect:
        colorize("# comment").contains("<span class=\"cd\"># comment</span>")
    }

    def "// comment should colorize"(){
        expect:
        colorize("// comment").contains("<span class=\"cd\">// comment</span>")
    }

    def "/* */ comment should colorize"(){
        expect:
        colorize("/* comment */").contains("<span class=\"cppd\">/* comment */</span>")
    }

    def "should colorize short string literals"(){
        expect:
        colorize('"string"').contains("<span class=\"s\">\"string\"</span>")
    }

    def "should colorize long string literals"(){
        expect:
        colorize('"string"').contains('<span class="s">"string"</span>')
    }

    private String colorize(String sourceCode) {
        return codeColorizer.toHtml(new StringReader(sourceCode))
    }
}