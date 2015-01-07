package com.iadams.sonarqube.puppet.pplint;

import org.sonar.api.BatchExtension;
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.config.Settings;

@Properties(
    @Property(
        key = PplintConfiguration.PPLINT_KEY,
        defaultValue = "",
        name = "pplint executable",
        description = "Path to the pplint executable to use in pplint analysis. Set to empty to use the default one.",
        global = true,
        project = false
    ))
public class PplintConfiguration implements BatchExtension {
    public static final String PPLINT_KEY = "sonar.puppet.pplint";
    private final Settings conf;

    public PplintConfiguration(Settings conf) {
        this.conf = conf;
    }

    public String getPplintPath() {
        return conf.getString(PplintConfiguration.PPLINT_KEY);
    }
}
