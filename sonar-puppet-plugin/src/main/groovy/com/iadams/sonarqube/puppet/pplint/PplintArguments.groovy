package com.iadams.sonarqube.puppet.pplint

import com.google.common.base.Joiner
import com.google.common.collect.Iterables
import org.sonar.api.utils.command.Command
import org.sonar.api.utils.command.CommandExecutor

import java.util.regex.Matcher
import java.util.regex.Pattern
/**
 * Created by iwarapter
 */
class PplintArguments {

    private static final Pattern PPLINT_VERSION_PATTERN = Pattern.compile("(.* )([0-9]).([0-9]).([0-9])")
    private static final def ARGS_PPLINT_0_X = ["--log-format=\"%{fullpath} - %{check} %{linenumber} %{KIND} %{message}\""]
    private static final def ARGS_PPLINT_1_X = ['--log-format="%{fullpath} - %{check} %{line} %{KIND} %{message}"']

    private def arguments

    PplintArguments(Command command) {
        String pplintVersion = pplintVersion(command)
        this.arguments = pplintVersion.startsWith("0") ? ARGS_PPLINT_0_X : ARGS_PPLINT_1_X
    }

    private static String pplintVersion(Command command) {
        long timeout = 10000;
        CommandStreamConsumer out = new CommandStreamConsumer();
        CommandStreamConsumer err = new CommandStreamConsumer();
        CommandExecutor.create().execute(command, out, err, timeout);

        def outputLines = out.getData() + err.getData()
        for (String outLine : outputLines) {
            Matcher matcher = PPLINT_VERSION_PATTERN.matcher(outLine)
            if (matcher.matches()) {
                return matcher.group(2)
            }
        }
        String message = "Failed to determine puppet-lint version with command: \" ${command.toCommandLine()}, received ${outputLines.size()} line(s) of output:\n ${outputLines.join('\n')}"
        throw new IllegalArgumentException(message)
    }

    def arguments() {
        return arguments
    }
}
