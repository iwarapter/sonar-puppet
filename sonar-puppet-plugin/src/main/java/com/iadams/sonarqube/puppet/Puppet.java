package com.iadams.sonarqube.puppet;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.AbstractLanguage;

import java.util.List;

/**
 * Created by iwarapter
 */
public class Puppet extends AbstractLanguage {

    public static final String KEY = "pp";

    private static final String[] DEFAULT_FILE_SUFFIXES = {"pp"};

    private Settings settings;

    public Puppet(Settings setts) {
        super(KEY, "Puppet");
        this.settings = setts;
    }

    public String[] getFileSuffixes() {
        String[] suffixes = filterEmptyStrings(settings.getStringArray(PuppetPlugin.FILE_SUFFIXES_KEY));

        return suffixes.length == 0 ? Puppet.DEFAULT_FILE_SUFFIXES : suffixes;
    }

    private String[] filterEmptyStrings(String[] stringArray) {
        List<String> nonEmptyStrings = Lists.newArrayList();
        for (String string : stringArray) {
            if (StringUtils.isNotBlank(string.trim())) {
                nonEmptyStrings.add(string.trim());
            }
        }
        return nonEmptyStrings.toArray(new String[nonEmptyStrings.size()]);
    }

}
