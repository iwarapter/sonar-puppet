package com.iadams.sonarqube.puppet.pplint;

import org.sonar.api.utils.command.StreamConsumer;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by iwarapter
 */
public class CommandStreamConsumer implements StreamConsumer {

    private List<String> data = new LinkedList<String>();

    public void consumeLine(String line) {
        data.add(line);
    }

    public List<String> getData() {
        return data;
    }
}
