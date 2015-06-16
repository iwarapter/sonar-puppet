/**
 * Sonar Puppet Plugin
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Iain Adams
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
import org.sonar.api.batch.SensorContext
import org.sonar.api.batch.fs.InputFile
import org.sonar.api.batch.fs.internal.DefaultFileSystem
import org.sonar.api.batch.fs.internal.DefaultInputFile
import org.sonar.api.batch.rule.ActiveRules
import org.sonar.api.batch.rule.CheckFactory
import org.sonar.api.batch.rule.internal.ActiveRulesBuilder
import org.sonar.api.component.ResourcePerspectives
import org.sonar.api.issue.Issuable
import org.sonar.api.measures.CoreMetrics
import org.sonar.api.measures.FileLinesContext
import org.sonar.api.measures.FileLinesContextFactory
import org.sonar.api.resources.Project
import org.sonar.api.rule.RuleKey
import spock.lang.Specification

/**
 * @author iwarapter
 */
class PuppetSquidSensorSpec extends Specification {

	private PuppetSquidSensor sensor
	private DefaultFileSystem fs = new DefaultFileSystem()
	ResourcePerspectives perspectives

	def setup() {
		FileLinesContextFactory fileLinesContextFactory = Mock()
		FileLinesContext fileLinesContext = Mock()

		fileLinesContextFactory.createFor(_ as InputFile) >> fileLinesContext
		ActiveRules activeRules = (new ActiveRulesBuilder())
				.create(RuleKey.of(CheckList.REPOSITORY_KEY, "LineLength"))
				.setName("Lines should not be too long")
				.activate()
				.build();
		CheckFactory checkFactory = new CheckFactory(activeRules)
		perspectives = Mock()
		sensor = new PuppetSquidSensor( fileLinesContextFactory, fs, perspectives, checkFactory)
	}

	def "should execute on puppet project"() {
		when:
		Project project = Mock()

		then:
		sensor.toString() == "PuppetSquidSensor"
		sensor.shouldExecuteOnProject(project) == false

		when:
		fs.add(new DefaultInputFile("test.pp").setLanguage(Puppet.KEY))

		then:
		sensor.shouldExecuteOnProject(project) == true
	}

	def "should_analyse"() {
		given:
		String relativePath = "src/test/resources/com/iadams/sonarqube/puppet/code_chunks.pp"
		DefaultInputFile inputFile = new DefaultInputFile(relativePath).setLanguage(Puppet.KEY)
		inputFile.setAbsolutePath((new File(relativePath)).getAbsolutePath())
		fs.add(inputFile)

		Issuable issuable = Mock()
		Issuable.IssueBuilder issueBuilder = Mock()
		perspectives.as(_, _) >> issuable
		issuable.newIssueBuilder() >> issueBuilder
		issueBuilder.ruleKey(_)  >> issueBuilder
		issueBuilder.line(_) >> issueBuilder
		issueBuilder.message(_) >> issueBuilder

		Project project = new Project("key")
		SensorContext context = Mock()

		when:
		sensor.analyse(project, context)

		then:
		1 * context.saveMeasure(_, CoreMetrics.FILES, 1.0)
		//1 * context.saveMeasure(_, CoreMetrics.LINES, 1.0)
		/*verify(context).saveMeasure(Mockito.any(Resource.class), Mockito.eq(CoreMetrics.NCLOC), Mockito.eq(25.0));
		verify(context).saveMeasure(Mockito.any(Resource.class), Mockito.eq(CoreMetrics.STATEMENTS), Mockito.eq(23.0));
		verify(context).saveMeasure(Mockito.any(Resource.class), Mockito.eq(CoreMetrics.FUNCTIONS), Mockito.eq(4.0));
		verify(context).saveMeasure(Mockito.any(Resource.class), Mockito.eq(CoreMetrics.COMPLEXITY), Mockito.eq(4.0));*/
		1 * context.saveMeasure(_, CoreMetrics.CLASSES, 2.0)
		1 * context.saveMeasure(_, CoreMetrics.COMMENT_LINES, 2.0)

	}
}
