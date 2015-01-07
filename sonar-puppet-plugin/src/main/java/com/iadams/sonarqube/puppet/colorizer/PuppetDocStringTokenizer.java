package com.iadams.sonarqube.puppet.colorizer;

import org.sonar.colorizer.MultilinesDocTokenizer;

/**
 * Created by iwarapter
 */
public class PuppetDocStringTokenizer extends MultilinesDocTokenizer {

    public PuppetDocStringTokenizer(String tagBefore, String tagAfter) {
        super("\"\"\"", "\"\"\"", tagBefore, tagAfter);
    }
}
