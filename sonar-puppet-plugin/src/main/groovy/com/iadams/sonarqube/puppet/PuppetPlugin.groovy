package com.iadams.sonarqube.puppet

import com.google.common.collect.ImmutableList
import com.iadams.sonarqube.puppet.pplint.PplintConfiguration
import com.iadams.sonarqube.puppet.pplint.PplintRuleRepository
import com.iadams.sonarqube.puppet.pplint.PplintSensor
import com.iadams.sonarqube.puppet.core.Puppet
import com.iadams.sonarqube.puppet.core.PuppetSourceImporter
import org.sonar.api.SonarPlugin
import org.sonar.api.config.PropertyDefinition
import org.sonar.api.resources.Qualifiers

/**
 * Created by iwarapter
 */
class PuppetPlugin extends SonarPlugin {

    static final String FILE_SUFFIXES_KEY = "sonar.puppet.file.suffixes"

    List getExtensions() {
        return ImmutableList.of(

            PropertyDefinition.builder(FILE_SUFFIXES_KEY)
                    .name("File Suffixes")
                    .description("Comma-separated list of suffixes of Puppet files to analyze.")
                    .category("Puppet")
                    .onQualifiers(Qualifiers.PROJECT)
                    .defaultValue("pp")
                    .build(),

                Puppet.class,
                PuppetSourceImporter.class,

                PuppetDefaultProfile.class,

                // pplint
                PplintConfiguration.class,
                PplintSensor.class,
                PplintRuleRepository.class
        )
    }
}
