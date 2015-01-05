package com.iadams.sonarqube.puppet

import org.sonar.api.profiles.XMLProfileParser
import org.sonar.api.profiles.ProfileDefinition
import org.sonar.api.profiles.RulesProfile
import org.sonar.api.utils.ValidationMessages

/**
 * Created by iwarapter
 */
public class PuppetDefaultProfile extends ProfileDefinition {

    private final XMLProfileParser xmlProfileParser

    public PuppetDefaultProfile(XMLProfileParser xmlProfileParser) {
        this.xmlProfileParser = xmlProfileParser
    }

    @Override
    public RulesProfile createProfile(ValidationMessages messages) {
        return xmlProfileParser.parseResource(getClass().getClassLoader(),'com/iadams/sonarqube/puppet/pplint/PuppetLintProfile.xml' , messages)
    }

}