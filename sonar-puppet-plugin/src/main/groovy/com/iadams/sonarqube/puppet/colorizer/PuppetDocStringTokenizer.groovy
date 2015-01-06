package com.iadams.sonarqube.puppet.colorizer

import org.sonar.colorizer.MultilinesDocTokenizer

/**
 * Created by iwarapter
 */
class PuppetDocStringTokenizer extends MultilinesDocTokenizer {

    PuppetDocStringTokenizer(String tagBefore, String tagAfter) {
        super("\"\"\"", "\"\"\"", tagBefore, tagAfter)
    }
}