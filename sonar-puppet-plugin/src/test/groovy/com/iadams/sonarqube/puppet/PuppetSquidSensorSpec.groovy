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

import com.google.common.collect.ImmutableList
import org.sonar.api.batch.SensorContext
import org.sonar.api.component.ResourcePerspectives
import org.sonar.api.measures.CoreMetrics
import org.sonar.api.measures.FileLinesContext
import org.sonar.api.measures.FileLinesContextFactory
import org.sonar.api.profiles.RulesProfile
import org.sonar.api.resources.Project
import org.sonar.api.resources.ProjectFileSystem
import org.sonar.api.scan.filesystem.FileQuery
import org.sonar.api.scan.filesystem.ModuleFileSystem
import spock.lang.Specification

import java.nio.charset.Charset

/**
 * Created by iwarapter
 */
class PuppetSquidSensorSpec extends Specification {

	FileLinesContextFactory fileLinesContextFactory;
	FileLinesContext fileLinesContext

	def setup() {
		fileLinesContextFactory = Mock()
		fileLinesContext = Mock()

		fileLinesContextFactory.createFor(_) >> fileLinesContext
	}

	def "should execute on puppet project"() {
		given:
		Project project = Mock()
		ModuleFileSystem fs = Mock()
		RulesProfile profile = Mock()
		profile.getActiveRulesByRepository(_) >> []
		PuppetSquidSensor sensor = new PuppetSquidSensor(profile, fileLinesContextFactory, fs, Mock(ResourcePerspectives))

		when:
		1 * fs.files(_) >> []

		then:
		sensor.shouldExecuteOnProject(project) == false

		when:
		1 * fs.files(_) >> [Mock(File)]

		then:
		sensor.shouldExecuteOnProject(project) == true
	}

	def "should_analyse"() {
		given:
		ModuleFileSystem fs = Mock()
		fs.sourceCharset(_) >> Charset.forName("UTF-8")
		fs.files(_) >> ImmutableList.of(
				new File("src/test/resources/com/iadams/sonarqube/puppet/code_chunks.pp"))

		ProjectFileSystem pfs = Mock()
		pfs.getSourceDirs() >> ImmutableList.of(new File("src/test/resources/com/iadams/sonarqube/puppet/"))

		Project project = new Project("key")
		project.setFileSystem(pfs)
		SensorContext context = Mock()
		RulesProfile profile = Mock()
		profile.getActiveRulesByRepository(_) >> []
		PuppetSquidSensor sensor = new PuppetSquidSensor(profile, fileLinesContextFactory, fs, Mock(ResourcePerspectives))

		when:
		sensor.analyse(project, context)

		then:
		1 * context.saveMeasure(_, CoreMetrics.FILES, 1.0)
		/*1 * context.saveMeasure(_, CoreMetrics.LINES, 1.0)
		verify(context).saveMeasure(Mockito.any(Resource.class), Mockito.eq(CoreMetrics.NCLOC), Mockito.eq(25.0));
		verify(context).saveMeasure(Mockito.any(Resource.class), Mockito.eq(CoreMetrics.STATEMENTS), Mockito.eq(23.0));
		verify(context).saveMeasure(Mockito.any(Resource.class), Mockito.eq(CoreMetrics.FUNCTIONS), Mockito.eq(4.0));
		verify(context).saveMeasure(Mockito.any(Resource.class), Mockito.eq(CoreMetrics.CLASSES), Mockito.eq(1.0));
		verify(context).saveMeasure(Mockito.any(Resource.class), Mockito.eq(CoreMetrics.COMPLEXITY), Mockito.eq(4.0));
		1 * context.saveMeasure(_, CoreMetrics.COMMENT_LINES, 2.0)*/
	}

	def "test toString()"() {
		given:
		RulesProfile profile = Mock()
		profile.getActiveRulesByRepository(_) >> []
		PuppetSquidSensor sensor = new PuppetSquidSensor(profile, fileLinesContextFactory, null, Mock(ResourcePerspectives.class))

		expect:
		sensor.toString().equals("PuppetSquidSensor")
	}
}
