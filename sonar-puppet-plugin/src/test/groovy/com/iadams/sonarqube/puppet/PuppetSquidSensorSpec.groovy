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

import com.iadams.sonarqube.puppet.checks.CheckList
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.sonar.api.batch.SensorContext
import org.sonar.api.batch.fs.InputFile
import org.sonar.api.batch.fs.internal.DefaultFileSystem
import org.sonar.api.batch.fs.internal.DefaultInputFile
import org.sonar.api.batch.rule.ActiveRules
import org.sonar.api.batch.rule.CheckFactory
import org.sonar.api.batch.rule.internal.ActiveRulesBuilder
import org.sonar.api.component.ResourcePerspectives
import org.sonar.api.issue.NoSonarFilter
import org.sonar.api.measures.CoreMetrics
import org.sonar.api.measures.FileLinesContext
import org.sonar.api.measures.FileLinesContextFactory
import org.sonar.api.profiles.RulesProfile
import org.sonar.api.resources.Project
import org.sonar.api.rule.RuleKey
import spock.lang.Ignore
import spock.lang.Specification

class PuppetSquidSensorSpec extends Specification {

  @Rule
  TemporaryFolder temporaryFolder

  private PuppetSquidSensor sensor
  private DefaultFileSystem fs
  ResourcePerspectives perspectives

  def setup() {
    fs = new DefaultFileSystem(new File('.'))
    FileLinesContextFactory fileLinesContextFactory = Mock()
    FileLinesContext fileLinesContext = Mock()
    NoSonarFilter noSonarFilter = Mock()
    RulesProfile rulesProfile = Mock()

    fileLinesContextFactory.createFor(_ as InputFile) >> fileLinesContext
    ActiveRules activeRules = (new ActiveRulesBuilder())
      .create(RuleKey.of(CheckList.REPOSITORY_KEY, "LineLength"))
      .setName("Lines should not be too long")
      .activate()
      .build();
    CheckFactory checkFactory = new CheckFactory(activeRules)
    perspectives = Mock()
    sensor = new PuppetSquidSensor(fileLinesContextFactory, fs, perspectives, checkFactory, noSonarFilter, rulesProfile)
  }

  def "should execute on puppet project"() {
    when:
    Project project = Mock()

    then:
    sensor.toString() == "PuppetSquidSensor"
    !sensor.shouldExecuteOnProject(project)

    when:
    fs.add(new DefaultInputFile('',"test.pp").setLanguage(Puppet.KEY))

    then:
    sensor.shouldExecuteOnProject(project)
  }

  @Ignore('need to work out how best to test the new file system changes')
  def "should_analyse"() {
    given:
    String relativePath = "src/test/resources/com/iadams/sonarqube/puppet/code_chunks.pp"
    DefaultInputFile inputFile = new DefaultInputFile('',relativePath).setLanguage(Puppet.KEY)

    fs.add(inputFile)

    Project project = new Project(Puppet.KEY)
    SensorContext context = Mock()

    when:
    sensor.analyse(project, context)

    then:
    1 * context.saveMeasure(_, CoreMetrics.FILES, 1.0)
    1 * context.saveMeasure(_, CoreMetrics.LINES, 9.0)
    1 * context.saveMeasure(_, CoreMetrics.CLASSES, 2.0)
    1 * context.saveMeasure(_, CoreMetrics.COMMENT_LINES, 2.0)
  }

}
