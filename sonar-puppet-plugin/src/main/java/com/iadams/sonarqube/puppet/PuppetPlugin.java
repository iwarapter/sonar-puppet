package com.iadams.sonarqube.puppet;

import com.google.common.collect.ImmutableList;
import com.iadams.sonarqube.puppet.colorizer.PuppetColorizer;
import com.iadams.sonarqube.puppet.pplint.PplintConfiguration;
import com.iadams.sonarqube.puppet.pplint.PplintRuleRepository;
import com.iadams.sonarqube.puppet.pplint.PplintSensor;
import org.sonar.api.SonarPlugin;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;

import java.util.List;

/**
 * Created by iwarapter
 */
public class PuppetPlugin extends SonarPlugin {

    public static final String FILE_SUFFIXES_KEY = "sonar.puppet.file.suffixes";

    public List getExtensions() {
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
                PuppetColorizer.class,

                PuppetDefaultProfile.class,
                PuppetCommonRulesEngine.class,

                PplintConfiguration.class,
                PplintSensor.class,
                PplintRuleRepository.class);
    }
}
