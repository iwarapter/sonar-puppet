package com.iadams.sonarqube.puppet.core

import org.apache.commons.lang.StringUtils
import org.sonar.api.config.Settings
import org.sonar.api.resources.AbstractLanguage
import com.iadams.sonarqube.puppet.PuppetPlugin

/**
 * Created by iwarapter
 */
class Puppet extends AbstractLanguage {

    static final String KEY = 'pp'

    static final String[] DEFAULT_FILE_SUFFIXES = ['pp']

    private Settings settings;

    Puppet(Settings configuration) {
        super(KEY, "Puppet")
        this.settings = configuration
    }

    String[] getFileSuffixes() {
        String[] suffixes = settings.getStringArray(PuppetPlugin.FILE_SUFFIXES_KEY)
        if (suffixes == null || suffixes.length == 0) {
            suffixes = DEFAULT_FILE_SUFFIXES
        }
        return suffixes
    }
}