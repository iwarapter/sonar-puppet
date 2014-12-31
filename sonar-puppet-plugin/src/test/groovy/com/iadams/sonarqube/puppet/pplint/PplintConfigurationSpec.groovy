package com.iadams.sonarqube.puppet.pplint

import org.sonar.api.config.Settings
import spock.lang.Specification

/**
 * Created by iwarapter
 */
class PplintConfigurationSpec extends Specification {

    private Settings settings
    private PplintConfiguration pplintConfiguration

    def setup(){
        settings = new Settings()
        pplintConfiguration = new PplintConfiguration(settings)
    }

    def "GetPplintPath"() {
        given:
        def path = "test/path"
        settings.setProperty(PplintConfiguration.PPLINT_KEY, path)

        expect:
        pplintConfiguration.getPplintPath() == path
    }
}
