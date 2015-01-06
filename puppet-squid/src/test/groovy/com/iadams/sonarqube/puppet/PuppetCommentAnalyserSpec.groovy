package com.iadams.sonarqube.puppet

import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by iwarapter on 23/12/14.
 */
class PuppetCommentAnalyserSpec extends Specification {

    PuppetCommentAnalyser analyser

    def setup(){
        analyser = new PuppetCommentAnalyser()
    }

    @Unroll
    def "check line with #testFor is blank #output"() {
        expect:
        analyser.isBlank(input) == output

        where:
        input   | output    | testFor
        '    '  | true      | 'spaces'
        '   '   | true      | 'tabs'
        'words' | false     | 'letters'
        '12345' | false     | 'numbers'
        '12wor' | false     | 'letters and numbers'
    }

    //https://docs.puppetlabs.com/puppet/latest/reference/lang_comments.html

    @Unroll
    def "get comments"(){
        expect:
        analyser.getContents(input) == output

        where:
        input           | output
        '# comment'     | ' comment'
        '// comment'    | ' comment'
        '/* comment */' | ' comment '
    }

    def "unknown comment type"(){
        when:
        analyser.getContents('')

        then:
        thrown(IllegalArgumentException)
    }
}
