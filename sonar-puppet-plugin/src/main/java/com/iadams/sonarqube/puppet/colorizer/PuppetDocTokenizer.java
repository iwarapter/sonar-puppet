package com.iadams.sonarqube.puppet.colorizer;

import org.sonar.colorizer.InlineDocTokenizer;

/**
 * Created by iwarapter
 */
public class PuppetDocTokenizer extends InlineDocTokenizer {

    public PuppetDocTokenizer(String tagBefore, String tagAfter) {
        super("#", tagBefore, tagAfter);
    }
}
