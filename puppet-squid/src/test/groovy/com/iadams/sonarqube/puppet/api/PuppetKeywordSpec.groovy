package com.iadams.sonarqube.puppet.api

import spock.lang.Specification

/**
 * Created by iwarapter on 16/12/14.
 */
class PuppetKeywordSpec extends Specification {
    def "KeywordValues"() {
        expect:
        PuppetKeyword.values().size() == 18
        PuppetKeyword.keywordValues().size() == PuppetKeyword.values().size()
    }
}
