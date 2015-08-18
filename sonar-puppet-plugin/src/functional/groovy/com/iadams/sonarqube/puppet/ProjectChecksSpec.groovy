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
package com.iadams.sonarqube.puppet

import com.iadams.sonarqube.functional.FunctionalSpecBase

class ProjectChecksSpec extends FunctionalSpecBase {

  def "check for tests directory in module"() {
    when:
    deleteProject()
    createPuppetModule('sonarqube')
    createPuppetModuleTestsDir('sonarqube')
    createPuppetModuleManfiest('sonarqube')

    runSonarRunner()

    then:
    analysisFinishedSuccessfully()
    theFollowingProjectMetricsHaveTheFollowingValue([violations: 1, lines: 2])
  }

  def "check module without manifest"() {
    when:
    deleteProject()
    createPuppetModule('sonarqube')

    runSonarRunner()

    then:
    analysisFinishedSuccessfully()
    theFollowingProjectMetricsHaveTheFollowingValue([violations: 1, lines: 2])
  }

  private void createPuppetModule(String moduleName, String baseDir = "$projectDir/modules"){
    String modulePath = directory(moduleName, new File(baseDir))
    directory('manifests', new File(modulePath))
    String specPath = directory('spec', new File(modulePath))
    directory('classes', new File(specPath))
    file("manifests/init.pp", new File(modulePath)) << "class $moduleName { }\n"
  }

  private void createPuppetModuleTestsDir(String moduleName, String baseDir = "$projectDir/modules"){
    directory("$moduleName/tests", new File(baseDir))
  }

  private void createPuppetModuleManfiest(String moduleName, String baseDir = "$projectDir/modules"){
    file("$moduleName/metadata.json", new File(baseDir))
  }
}
