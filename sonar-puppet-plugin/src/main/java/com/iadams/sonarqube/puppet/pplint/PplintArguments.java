/**
 * Sonar Puppet Plugin
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

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import org.sonar.api.utils.command.Command;
import org.sonar.api.utils.command.CommandExecutor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by iwarapter
 */
public class PplintArguments {

    private static final Pattern PPLINT_VERSION_PATTERN = Pattern.compile("(.* )([0-9]).([0-9]).([0-9])");
    private static final String[] ARGS_PPLINT_0_X = {"--log-format=\"%{fullpath} - %{check} %{linenumber} %{KIND} %{message}\""};
    private static final String[] ARGS_PPLINT_1_X = {"--log-format=\"%{fullpath} - %{check} %{line} %{KIND} %{message}\""};

    private final String[] arguments;

    public PplintArguments(Command command) {
        String pplintVersion = pplintVersion(command);
        this.arguments = pplintVersion.startsWith("0") ? ARGS_PPLINT_0_X : ARGS_PPLINT_1_X;
    }

    private static String pplintVersion(final Command command) {
        long timeout = 10000;
        CommandStreamConsumer out = new CommandStreamConsumer();
        CommandStreamConsumer err = new CommandStreamConsumer();
        CommandExecutor.create().execute(command, out, err, timeout);

        Iterable<String> outputLines = Iterables.concat(out.getData(), err.getData());
        for (String outLine : outputLines) {
            Matcher matcher = PPLINT_VERSION_PATTERN.matcher(outLine);
            if (matcher.matches()) {
                return matcher.group(2);
            }
        }
        String message =
                "Failed to determine pylint version with command: \"" + command.toCommandLine()
                        + "\", received " + Iterables.size(outputLines) + " line(s) of output:\n" + Joiner.on('\n').join(outputLines);
        throw new IllegalArgumentException(message);
    }

    public String[] arguments() {
        return arguments.clone();
    }
}
