package com.iadams.sonarqube.puppet;

import org.sonar.api.batch.AbstractSourceImporter;

/**
 * Created by iwarapter
 */
public class PuppetSourceImporter extends AbstractSourceImporter {

    public PuppetSourceImporter(Puppet puppet) {
        super(puppet);
    }
}
