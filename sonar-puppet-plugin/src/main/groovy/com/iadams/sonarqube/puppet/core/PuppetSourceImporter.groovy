package com.iadams.sonarqube.puppet.core

import org.sonar.api.batch.AbstractSourceImporter

/**
 * Created by iwarapter
 */
class PuppetSourceImporter extends AbstractSourceImporter {

    PuppetSourceImporter(Puppet puppet) {
        super(puppet)
    }
}