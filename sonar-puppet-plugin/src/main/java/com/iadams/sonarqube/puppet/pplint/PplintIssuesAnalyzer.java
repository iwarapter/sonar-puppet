/*
 * SonarQube Puppet Plugin
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Iain Adams
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.iadams.sonarqube.puppet.pplint;

import com.google.common.io.Files;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.utils.SonarException;
import org.sonar.api.utils.command.Command;
import org.sonar.api.utils.command.CommandExecutor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PplintIssuesAnalyzer {

    private static final Logger LOG = LoggerFactory.getLogger(PplintSensor.class);

    private static final String FALLBACK_PPLINT = "puppet-lint";
    private static final Pattern PATTERN = Pattern.compile("\"(.*) - ([a-z0-9_]+) ([0-9]+) ([A-Z]+ )(.*)\"");
    private String pplint = null;
    private PplintArguments pplintArguments;

    public PplintIssuesAnalyzer(String path) {
        this(path, new PplintArguments(Command.create(pplintPath(path)).addArgument("--version")));
    }

    public PplintIssuesAnalyzer(String path, PplintArguments arguments) {
        pplint = pplintPath(path);
        pplintArguments = arguments;
    }

    private static String pplintPath(final String path) {
        if (path != null) {
            if (!new File(path).exists()) {
                throw new SonarException("Cannot find the pplint executable: " + path);
            }
            return path;
        }

        return FALLBACK_PPLINT;
    }

    public List<Issue> analyze(String path, Charset charset, File out) throws IOException {
        Command command = Command.create(pplint).addArgument(path).addArguments(pplintArguments.arguments());

        LOG.debug("Calling command: " + command.toString());

        long timeoutMS = 300000;// =5min
        CommandStreamConsumer stdOut = new CommandStreamConsumer();
        final CommandStreamConsumer stdErr = new CommandStreamConsumer();
        CommandExecutor.create().execute(command, stdOut, stdErr, timeoutMS);

        // log any std error output
        if (stdErr.getData().size() > 0) {
            LOG.warn("Output on the error channel detected: this is probably due to a problem on pplint's side.");
            LOG.warn("Content of the error stream: \n\"{}\"", StringUtils.join(stdErr.getData(), "\n"));
        }


        Files.write(StringUtils.join(stdOut.getData(), "\n"), out, charset);

        return parseOutput(stdOut.getData());
    }

    protected List<Issue> parseOutput(List<String> lines) {
        // Parse the output of pylint. Example of the format:
        //
        // "/vagrant/code/NestedClasses.pp - nested_classes_or_defines 4 WARNING class defined inside a class"
        // "/vagrant/code/NestedClasses.pp - autoloader_layout 2 ERROR apache not in autoload module layout"
        // "/vagrant/code/NestedClasses.pp - autoloader_layout 4 IGNORED ssl not in autoload module layout"

        List<Issue> issues = new LinkedList<Issue>();

        if (!lines.isEmpty()) {
            for (String line : lines) {
                if (line.length() > 0) {
                    Matcher m = PATTERN.matcher(line);
                    if (m.matches() && m.groupCount() == 5) {
                        String filename = m.group(1);
                        int linenr = Integer.parseInt(m.group(3));
                        String ruleid = m.group(2);

                        if(m.group(4).equals("IGNORED ")){
                            ruleid = "IgnoredPuppetLintRule";
                        }

                         String descr = m.group(5);
                         issues.add(new Issue(filename, linenr, ruleid, descr));
                    } else {
                        LOG.debug("Cannot parse the line: {}", line);
                    }
                } else {
                    LOG.trace("Classifying as detail and ignoring line '{}'", line);
                }
            }
        }

        return issues;
    }
}
