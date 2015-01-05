package com.iadams.sonarqube.puppet.pplint

import org.sonar.api.utils.command.StreamConsumer

/**
 * Created by iwarapter
 */
class CommandStreamConsumer implements StreamConsumer {
    private def data = []

    void consumeLine(String line) {
        data.add(line)
    }

    def getData() {
        return data
    }
}