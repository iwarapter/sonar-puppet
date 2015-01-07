package com.iadams.sonarqube.puppet.pplint;

/**
 * Created by iwarapter
 */
public class Issue {

    private final String filename;
    private final int line;
    private final String ruleId;
    private final String descr;

    Issue(String filename, int line, String ruleId, String descr) {
        this.filename = filename;
        this.line = line;
        this.ruleId = ruleId;
        this.descr = descr;
    }

    @Override
    public String toString() {
        return "(" + filename + ", " + line + ", " + ruleId + ", " + descr + ")";
    }

    String getFilename() {
        return filename;
    }

    int getLine() {
        return line;
    }

    String getRuleId() {
        return ruleId;
    }

    String getDescr() {
        return descr;
    }
}
