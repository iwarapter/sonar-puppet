package com.iadams.sonarqube.puppet

import org.sonar.api.SonarPlugin

/**
 * Created by iwarapter on 04/12/14.
 */
class PuppetPlugin extends SonarPlugin {

    // Global JavaScript constants
    public static final String FALSE = "false"

    public static final String PROPERTY_PREFIX = "sonar.puppet"

    public static final String FILE_SUFFIXES_KEY = "${PROPERTY_PREFIX}.file.suffixes"
    public static final String FILE_SUFFIXES_DEFVALUE = ".pp"

    public List getExtensions() {
        return ImmutableList.of(
            Puppet.class,
            PuppetMetrics.class,
            PuppetSensor.class,
            PuppetDashboardWidget.class,

        PropertyDefinition.builder(FILE_SUFFIXES_KEY)
                .defaultValue(FILE_SUFFIXES_DEFVALUE)
                .name("File Suffixes")
                .description("Comma-separated list of suffixes for files to analyze.")
                .build()
        );
    }
}