package com.iadams.sonarqube.puppet;

import org.sonar.squidbridge.api.SquidConfiguration;

import java.nio.charset.Charset;
/**
 * Created by iwarapter
 */
public class PuppetConfiguration extends SquidConfiguration {

    private boolean ignoreHeaderComments;

    public PuppetConfiguration(Charset charset) {
        super(charset);
    }

    public void setIgnoreHeaderComments(boolean ignoreHeaderComments) {
        this.ignoreHeaderComments = ignoreHeaderComments;
    }

    public boolean getIgnoreHeaderComments() {
        return ignoreHeaderComments;
    }

}
