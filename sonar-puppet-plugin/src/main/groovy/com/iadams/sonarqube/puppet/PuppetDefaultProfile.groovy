package com.iadams.sonarqube.puppet

import com.iadams.sonarqube.puppet.core.Puppet
import com.iadams.sonarqube.puppet.pplint.PplintRuleRepository
import org.sonar.api.profiles.AnnotationProfileParser
import org.sonar.api.profiles.ProfileDefinition
import org.sonar.api.profiles.RulesProfile
import org.sonar.api.utils.ValidationMessages

/**
 * Created by iwarapter
 */
public class PuppetDefaultProfile extends ProfileDefinition {

    static final String DEFAULT_PUPPET_PROFILE = "Puppet Lint"

    private final AnnotationProfileParser annotationProfileParser;

    public PuppetDefaultProfile(AnnotationProfileParser annotationProfileParser) {
        this.annotationProfileParser = annotationProfileParser;
    }

    @Override
    public RulesProfile createProfile(ValidationMessages messages) {
        return annotationProfileParser.parse(PplintRuleRepository.REPOSITORY_KEY, DEFAULT_PUPPET_PROFILE, Puppet.KEY,messages)
    }

}