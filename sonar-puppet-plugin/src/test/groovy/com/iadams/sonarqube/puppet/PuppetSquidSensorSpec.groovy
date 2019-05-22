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

import com.google.common.base.Charsets
import com.iadams.sonarqube.puppet.checks.CheckList
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.sonar.api.batch.fs.InputFile
import org.sonar.api.batch.fs.InputFile.Type
import org.sonar.api.batch.fs.internal.DefaultInputFile
import org.sonar.api.batch.fs.internal.DefaultTextRange
import org.sonar.api.batch.fs.internal.FileMetadata
import org.sonar.api.batch.rule.ActiveRules
import org.sonar.api.batch.rule.CheckFactory
import org.sonar.api.batch.rule.internal.ActiveRulesBuilder
import org.sonar.api.batch.sensor.internal.DefaultSensorDescriptor
import org.sonar.api.batch.sensor.internal.SensorContextTester
import org.sonar.api.batch.sensor.issue.Issue
import org.sonar.api.batch.sensor.issue.IssueLocation
import org.sonar.api.issue.NoSonarFilter
import org.sonar.api.measures.CoreMetrics
import org.sonar.api.measures.FileLinesContext
import org.sonar.api.measures.FileLinesContextFactory
import org.sonar.api.rule.RuleKey
import org.sonar.api.utils.log.LogTester
import spock.lang.Specification

class PuppetSquidSensorSpec extends Specification {

  @Rule
  TemporaryFolder folder = new TemporaryFolder()

  @Rule
  LogTester logTester = new LogTester()

  private final File baseDir = new File("src/test/resources/com/iadams/sonarqube/puppet/squid-sensor")
  private SensorContextTester context = SensorContextTester.create(baseDir)
  private ActiveRules activeRules

  def "sensor descriptor"() {
    given:
    activeRules = (new ActiveRulesBuilder()).build()
    DefaultSensorDescriptor descriptor = new DefaultSensorDescriptor()
    sensor().describe(descriptor)

    expect:
    descriptor.name() == "Puppet Squid Sensor"
    descriptor.languages().size() == 1
    descriptor.languages().contains('pp')
    descriptor.type() == Type.MAIN
  }

  def "test execute"() {
    given:
    activeRules = (new ActiveRulesBuilder())
      .create(RuleKey.of(CheckList.REPOSITORY_KEY, "QuotedBoolean"))
      .setName("Booleans should not be quoted")
      .activate()
      .build()

    inputFile("file1.pp")

    sensor().execute(context)

    String key = "moduleKey:file1.pp"

    expect:
    context.measure(key, CoreMetrics.NCLOC).value() == 14
    // Sonarqube 7.7: java.lang.UnsupportedOperationException: Metric 'files' should not be computed by a Sensor
    // context.measure(key, CoreMetrics.FILES).value() == 1
    context.measure(key, CoreMetrics.STATEMENTS).value() == 7
    context.measure(key, CoreMetrics.CLASSES).value() == 2
    context.measure(key, CoreMetrics.COMPLEXITY).value() == 9
    context.measure(key, CoreMetrics.COMMENT_LINES).value() == 7

    context.allIssues().size() == 1
  }

  def "test issues correct"() {
    given:
    activeRules = (new ActiveRulesBuilder())
      .create(RuleKey.of(CheckList.REPOSITORY_KEY, "QuotedBoolean"))
      .activate()
      .create(RuleKey.of(CheckList.REPOSITORY_KEY, "S1862"))
      .activate()
      .create(RuleKey.of(CheckList.REPOSITORY_KEY, "ComplexExpression"))
      .setParam('max', '2')
      .activate()
      .build()

    InputFile inputFile = inputFile("file1.pp")

    when:
    sensor().execute(context)

    then:
    context.allIssues().size() == 3
    Iterator<Issue> issuesIterator = context.allIssues().iterator()

    int checkedIssues = 0

    while (issuesIterator.hasNext()) {
      Issue issue = issuesIterator.next()
      IssueLocation issueLocation = issue.primaryLocation()
      issueLocation.inputComponent() == inputFile
DefaultTextRange
      if (issue.ruleKey().rule() == "S1862") {
        assert issueLocation.message() == 'This branch duplicates the one on line 17.'
        assert issueLocation.textRange() == inputFile.newRange(23, 0, 23, 33)
        assert issue.flows().isEmpty()
        assert issue.gap() == null
        checkedIssues++
      } else if (issue.ruleKey().rule() == "QuotedBoolean") {
        assert issueLocation.message() == "Remove quotes."
        assert issueLocation.textRange() == inputFile.newRange(14, 0, 14, 18)
        assert issue.flows().isEmpty()
        assert issue.gap() == null
        checkedIssues++
      } else if (issue.ruleKey().rule() == "ComplexExpression") {
        assert issueLocation.message() == "Reduce the number of boolean operators. This condition contains 4 boolean operators, 2 more than the 2 maximum."
        assert issueLocation.textRange() == inputFile.newRange(11, 0, 11, 103)
        assert issue.flows().isEmpty()
        assert issue.gap() == null
        checkedIssues++
      } else {
        throw new IllegalStateException()
      }
    }

    checkedIssues == 3
  }

  def "parse_error"() {
    given:
    inputFile("parsing_error.pp")
    activeRules = (new ActiveRulesBuilder())
      .create(RuleKey.of(CheckList.REPOSITORY_KEY, "ParsingError"))
      .activate()
      .build()

    when:
    sensor().execute(context)

    then:
    context.allIssues().size() == 1
    context.allIssues()[0].primaryLocation().message() == 'Parse error at line 1 column 13:\n\n  -->  file { \'name\';}EOF'
  }

  private PuppetSquidSensor sensor() {
    FileLinesContextFactory fileLinesContextFactory = Mock(FileLinesContextFactory)
    FileLinesContext fileLinesContext = Mock(FileLinesContext)
    fileLinesContextFactory.createFor(_ as InputFile) >> fileLinesContext
    CheckFactory checkFactory = new CheckFactory(activeRules)
    return new PuppetSquidSensor(fileLinesContextFactory, checkFactory, new NoSonarFilter())
  }

  private InputFile inputFile(String name) {
    DefaultInputFile inputFile = new DefaultInputFile("moduleKey", name)
      .setModuleBaseDir(baseDir.toPath())
      .setType(Type.MAIN)
      .setLanguage(Puppet.KEY)
    context.fileSystem().add(inputFile)
    inputFile.initMetadata(new FileMetadata().readMetadata(inputFile.file(), Charsets.UTF_8))
    return inputFile
  }
}
