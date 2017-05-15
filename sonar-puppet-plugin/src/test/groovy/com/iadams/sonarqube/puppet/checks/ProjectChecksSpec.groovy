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

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.sonar.api.batch.fs.internal.DefaultInputDir
import org.sonar.api.batch.fs.internal.DefaultInputFile
import org.sonar.api.batch.rule.internal.ActiveRulesBuilder
import org.sonar.api.batch.sensor.internal.SensorContextTester
import org.sonar.api.rule.RuleKey
import spock.lang.Specification

class ProjectChecksSpec extends Specification {

  @Rule
  TemporaryFolder testProjectDir = new TemporaryFolder()

  private SensorContextTester context

  ProjectChecks projectChecks

  def setup() {
    context = SensorContextTester.create(testProjectDir.root)
    context.activeRules = new ActiveRulesBuilder()
      .create(RuleKey.of(CheckList.REPOSITORY_KEY, ReadmeFilePresentCheck.RULE_KEY)).activate()
      .create(RuleKey.of(CheckList.REPOSITORY_KEY, MetadataJsonFilePresentCheck.RULE_KEY)).activate()
      .create(RuleKey.of(CheckList.REPOSITORY_KEY, TestsDirectoryPresentCheck.RULE_KEY)).activate()
      .build()
    projectChecks = new ProjectChecks(context)
  }

  def "readme file present check"() {
    given:
    testProjectDir.newFolder('manifests')
    testProjectDir.newFile('metadata.json')
    context.fileSystem().add(new DefaultInputDir("myProjectKey", 'manifests').setModuleBaseDir(testProjectDir.root.toPath()))
    context.fileSystem().add(new DefaultInputFile("myProjectKey", "metadata.json").setModuleBaseDir(testProjectDir.root.toPath()).initMetadata('stuff'))

    when:
    projectChecks.reportProjectIssues()

    then:
    noExceptionThrown()
    context.allIssues().size() == 1
    context.allIssues()[0].ruleKey() == RuleKey.of(CheckList.REPOSITORY_KEY, ReadmeFilePresentCheck.RULE_KEY)
  }

  def "manifest file present check"() {
    given:
    testProjectDir.newFolder('manifests')
    testProjectDir.newFile('README.md')
    context.fileSystem().add(new DefaultInputDir("myProjectKey", 'manifests').setModuleBaseDir(testProjectDir.root.toPath()))
    context.fileSystem().add(new DefaultInputFile("myProjectKey", "README.md").setModuleBaseDir(testProjectDir.root.toPath()).initMetadata('stuff'))

    when:
    projectChecks.reportProjectIssues()

    then:
    noExceptionThrown()
    context.allIssues().size() == 1
    context.allIssues()[0].ruleKey() == RuleKey.of(CheckList.REPOSITORY_KEY, MetadataJsonFilePresentCheck.RULE_KEY)
    context.allIssues()[0].primaryLocation().message()
  }


  def "'tests' directory present check"() {
    given:
    testProjectDir.newFolder('manifests')
    testProjectDir.newFolder('tests')
    testProjectDir.newFile('README.md')
    testProjectDir.newFile('metadata.json')
    context.fileSystem().add(new DefaultInputDir("myProjectKey", 'tests').setModuleBaseDir(testProjectDir.root.toPath()))

    when:
    projectChecks.reportProjectIssues()

    then:
    noExceptionThrown()
    context.allIssues().size() == 1
    context.allIssues()[0].ruleKey() == RuleKey.of(CheckList.REPOSITORY_KEY, TestsDirectoryPresentCheck.RULE_KEY)
  }
}
