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
package com.iadams.sonarqube.puppet.checks

import com.iadams.sonarqube.puppet.PuppetAstScanner
import org.sonar.squidbridge.api.SourceFile
import org.sonar.squidbridge.checks.CheckMessagesVerifier
import spock.lang.Specification
import spock.lang.Unroll

class AutoLoaderLayoutCheckSpec extends Specification {

  @Unroll
  def "files in correct location #puppetFile"() {
    given:
    SourceFile file = PuppetAstScanner.scanSingleFile(
      new File(puppetFile),
      new AutoLoaderLayoutCheck()
    );

    expect:
    CheckMessagesVerifier.verify(file.getCheckMessages())
      .noMore();

    where:
    puppetFile << [
      "src/test/resources/checks/autoloader/foo/manifests/bar.pp",
      "src/test/resources/checks/autoloader/foo/manifests/init.pp",
      "src/test/resources/checks/autoloader/foo/manifests/bar/baz.pp",
      "src/test/resources/checks/autoloader/bar/manifests/init.pp"
    ]
  }

  def "file in wrong location"() {
    given:
    SourceFile file = PuppetAstScanner.scanSingleFile(
      new File("src/test/resources/checks/autoloader/puppet-foo/manifests/init.pp"),
      new AutoLoaderLayoutCheck()
    );

    expect:
    CheckMessagesVerifier.verify(file.getCheckMessages())
      .next().withMessage('"init.pp" not in autoload module layout')
      .noMore();
  }
}
