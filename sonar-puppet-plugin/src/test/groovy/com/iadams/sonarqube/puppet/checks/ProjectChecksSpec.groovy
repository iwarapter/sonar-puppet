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

import com.sonar.sslr.api.Grammar
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.sonar.api.batch.fs.internal.DefaultFileSystem
import org.sonar.api.batch.rule.ActiveRules
import org.sonar.api.batch.rule.CheckFactory
import org.sonar.api.batch.rule.Checks
import org.sonar.api.batch.rule.internal.ActiveRulesBuilder
import org.sonar.api.component.ResourcePerspectives
import org.sonar.api.profiles.RulesProfile
import org.sonar.api.resources.Project
import org.sonar.api.resources.ProjectFileSystem
import org.sonar.api.rule.RuleKey
import org.sonar.squidbridge.SquidAstVisitor
import spock.lang.Specification

class ProjectChecksSpec extends Specification {

  @Rule
  TemporaryFolder testProjectDir = new TemporaryFolder()

  ProjectChecks projectChecks;
  DefaultFileSystem fs = new DefaultFileSystem()
  Project project
  Checks<SquidAstVisitor<Grammar>> checks
  Map<String, String> issues;

  def setup() {
    fs.setBaseDir(testProjectDir.root)
    ProjectFileSystem pfs = Mock()
    project = Mock()
    project.getFileSystem() >> pfs
    pfs.getBasedir() >> fs.baseDir()
    ActiveRules activeRules = (new ActiveRulesBuilder())
      .create(RuleKey.of(CheckList.REPOSITORY_KEY, ReadmeFilePresentCheck.RULE_KEY))
      .activate()
      .build();
    CheckFactory checkFactory = new CheckFactory(activeRules)
    checks = checkFactory
      .<SquidAstVisitor<Grammar>> create(CheckList.REPOSITORY_KEY)
      .addAnnotatedChecks(CheckList.getChecks());

    issues = new HashMap<>();

    projectChecks = Spy(ProjectChecks, constructorArgs: [project, fs, Mock(RulesProfile), checks, Mock(ResourcePerspectives)])
    projectChecks.addIssue(_, _) >> { String ruleKey, String message -> issues.put(ruleKey, message) }
  }

  def "readme file present check"() {
    given:
    testProjectDir.newFolder('manifests')
    testProjectDir.newFile('metadata.json')

    when:
    projectChecks.reportProjectIssues()

    then:
    noExceptionThrown()
    issues.size() == 1
    issues.containsKey(ReadmeFilePresentCheck.RULE_KEY)
  }

  def "manifest file present check"() {
    given:
    testProjectDir.newFolder('manifests')
    testProjectDir.newFile('README.md')

    when:
    projectChecks.reportProjectIssues()

    then:
    noExceptionThrown()
    issues.size() == 1
    issues.containsKey(MetadataJsonFilePresentCheck.RULE_KEY)
  }


  def "'tests' directory present check"() {
    given:
    testProjectDir.newFolder('manifests')
    testProjectDir.newFolder('tests')
    testProjectDir.newFile('README.md')
    testProjectDir.newFile('metadata.json')

    when:
    projectChecks.reportProjectIssues()

    then:
    noExceptionThrown()
    issues.size() == 1
    issues.containsKey(TestsDirectoryPresentCheck.RULE_KEY)
  }
}
