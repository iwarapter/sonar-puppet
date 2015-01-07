package com.iadams.sonarqube.puppet

import org.sonar.api.config.Settings
import spock.lang.Specification

/**
 * Created by iwarapter
 */
class PuppetSpec extends Specification {


    def "check language properties are set"(){
        given:
        Puppet language = new Puppet(new Settings())

        expect:
        language.getKey() == 'pp'
        language.getName() == 'Puppet'
        language.getFileSuffixes().size() == 1
        language.getFileSuffixes() == ['pp']
    }

    def "add custom file suffixes"() {
        given:
        def props = [:]
        props.put(PuppetPlugin.FILE_SUFFIXES_KEY, 'pp, puppet')

        Settings settings = new Settings()
        settings.addProperties(props)
        Puppet language = new Puppet(settings)

        expect:
        language.getFileSuffixes().size() == 2
        language.getFileSuffixes().contains('pp')
        language.getFileSuffixes().contains('puppet')
    }
}