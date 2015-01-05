package com.iadams.sonarqube.puppet.pplint

import org.apache.commons.lang.SystemUtils
import org.sonar.api.utils.command.Command
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by iwarapter
 */
class PplintArgumentsSpec extends Specification {

    def "unknown puppet-lint exec return error"() {
        when:
        new PplintArguments(command(""))

        then:
        thrown(IllegalArgumentException)
    }

    @Unroll
    def "version #version return correct arguments"(){
        expect:
        new PplintArguments(command(version)).arguments() == args

        where:
        version             | args
        "puppet-lint 1.1.0" | ['--log-format="%{fullpath} - %{check} %{line} %{KIND} %{message}"']
        "Puppet-lint 0.3.2" | ['--log-format="%{fullpath} - %{check} %{linenumber} %{KIND} %{message}"']
    }

    private Command command(String toOutput) {
        if (SystemUtils.IS_OS_WINDOWS) {
            return Command.create("cmd.exe").addArguments(["/c", "echo", toOutput])
        }
        return Command.create("echo").addArgument(toOutput)
    }
}
