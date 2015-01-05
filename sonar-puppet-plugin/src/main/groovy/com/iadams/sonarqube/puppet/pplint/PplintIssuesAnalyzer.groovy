package com.iadams.sonarqube.puppet.pplint

import groovy.util.logging.Slf4j
import org.sonar.api.utils.SonarException
import org.sonar.api.utils.command.Command
import org.sonar.api.utils.command.CommandExecutor

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by iwarapter
 */
@Slf4j
class PplintIssuesAnalyzer {

    private static final String FALLBACK_PPLINT = "puppet-lint"
    private static final Pattern PATTERN = Pattern.compile("\"(.*) - ([a-z0-9_]+) ([0-9]+ )([A-Z]+ )(.*)\"")

    private String pplint = null
    private PplintArguments pplintArguments

    PplintIssuesAnalyzer(String path) {
        this(path, new PplintArguments(Command.create(pplintPath(path)).addArgument("--version")))
    }

    PplintIssuesAnalyzer(String path, PplintArguments arguments){
        pplint = pplintPath(path)
        pplintArguments = arguments
    }

    private static String pplintPath(String path) {
        if (path != null) {
            if (!new File(path).exists()) {
                throw new SonarException("Cannot find the pplint executable: ${path}")
            }
            return path
        }
        return FALLBACK_PPLINT
    }

    def analyze(String path, File out) throws IOException {
        Command command = Command.create(pplint).addArgument(path).addArguments(pplintArguments.arguments())

        log.debug "Calling command: ${command.toString()}"

        long timeoutMS = 300000; // =5min
        CommandStreamConsumer stdOut = new CommandStreamConsumer()
        CommandStreamConsumer stdErr = new CommandStreamConsumer()
        CommandExecutor.create().execute(command, stdOut, stdErr, timeoutMS)

        // log any std error output
        if (stdErr.getData().size() > 0) {
            log.warn "Content of the error stream: \n${stdErr.getData().join('\n')}"
        }

        out.write(stdOut.getData().join('\n'))

        return parseOutput(stdOut.getData())
    }

    protected def parseOutput(def lines) {
        // Parse the output of pylint. Example of the format:
        //
        // "/vagrant/code/NestedClasses.pp - nested_classes_or_defines 4 WARNING class defined inside a class"
        // "/vagrant/code/NestedClasses.pp - autoloader_layout 2 ERROR apache not in autoload module layout"
        // "/vagrant/code/NestedClasses.pp - autoloader_layout 4 IGNORED ssl not in autoload module layout"

        def issues = []

        lines.each{
            if (it.length() > 0) {
                Matcher m = PATTERN.matcher(it)
                if (m.matches() && m.groupCount() == 5) {
                    String filename = m.group(1)
                    int linenr = m.group(3).toInteger()
                    String ruleid = m.group(2)

                    if(m.group(4) == 'IGNORED '){
                        ruleid = 'IgnoredPuppetLintRule'
                    }

                    String descr = m.group(5)
                    issues.add(new Issue(filename, linenr, ruleid, descr))
                } else {
                    log.debug "Cannot parse the line: ${it}"
                }
            }
        }

        return issues
    }
}
