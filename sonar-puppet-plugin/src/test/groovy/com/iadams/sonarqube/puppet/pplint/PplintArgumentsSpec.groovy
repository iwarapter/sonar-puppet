/*
 * SonarQube Puppet Plugin
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Iain Adams and David RACODON
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
package com.iadams.sonarqube.puppet.pplint

import org.apache.commons.lang.SystemUtils
import org.sonar.api.utils.command.Command
import spock.lang.Specification
import spock.lang.Unroll

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
