package com.iadams.sonarqube.puppet.pplint

/**
 * Created by iwarapter
 */
class Issue {

    private final String filename
    private final int line
    private final String ruleId
    private final String descr

    Issue(String filename, int line, String ruleId, String descr) {
        this.filename = filename
        this.line = line
        this.ruleId = ruleId
        this.descr = descr
    }

    @Override
    String toString() {
        return "(${filename}, ${line}, ${ruleId}, ${descr})"
    }
}
