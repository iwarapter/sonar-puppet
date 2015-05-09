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
import com.iadams.sonarqube.puppet.metrics.FileLinesVisitor
import com.sonar.sslr.api.Grammar
import org.sonar.api.measures.CoreMetrics
import org.sonar.api.measures.FileLinesContext
import org.sonar.api.measures.FileLinesContextFactory
import org.sonar.api.resources.Project
import org.sonar.api.resources.ProjectFileSystem
import org.sonar.squidbridge.SquidAstVisitor
import org.sonar.squidbridge.api.SourceFile
import spock.lang.Specification

/**
 * Created by iwarapter
 */
class FileLinesVisitorSpec extends Specification {

	static final File BASE_DIR = new File("src/test/resources/metrics")

	Project project
	FileLinesContextFactory fileLinesContextFactory
	ProjectFileSystem fileSystem
	FileLinesContext fileLinesContext

	def setup(){
		project = Mock()
		fileLinesContextFactory = Mock()
		fileSystem = Mock()
		fileLinesContext = Mock()

		project.fileSystem >> fileSystem
		fileSystem.sourceDirs >> ImmutableList.of(BASE_DIR)
	}

	def "check metrics calculate correctly"() {
		when:
		File file = new File(BASE_DIR, "lines.pp")
		org.sonar.api.resources.File resource  = org.sonar.api.resources.File.fromIOFile(file, project)
		fileLinesContextFactory.createFor(resource) >> fileLinesContext

		SquidAstVisitor<Grammar> visitor = new FileLinesVisitor(project, fileLinesContextFactory);

		SourceFile sourceFile = PuppetAstScanner.scanSingleFile(file, visitor);

		then:
		1 * fileLinesContext.setIntValue(CoreMetrics.NCLOC_DATA_KEY, 1, 0)
		1 * fileLinesContext.setIntValue(CoreMetrics.NCLOC_DATA_KEY, 2, 0)
		1 * fileLinesContext.setIntValue(CoreMetrics.NCLOC_DATA_KEY, 3, 0)
		1 * fileLinesContext.setIntValue(CoreMetrics.NCLOC_DATA_KEY, 4, 0)
		1 * fileLinesContext.setIntValue(CoreMetrics.NCLOC_DATA_KEY, 5, 1)
		1 * fileLinesContext.setIntValue(CoreMetrics.COMMENT_LINES_DATA_KEY, 1, 1)
		1 * fileLinesContext.setIntValue(CoreMetrics.COMMENT_LINES_DATA_KEY, 2, 1)
		1 * fileLinesContext.setIntValue(CoreMetrics.COMMENT_LINES_DATA_KEY, 3, 1)
		1 * fileLinesContext.setIntValue(CoreMetrics.COMMENT_LINES_DATA_KEY, 4, 0)
		1 * fileLinesContext.setIntValue(CoreMetrics.COMMENT_LINES_DATA_KEY, 5, 0)
		1 * fileLinesContext.save()
	}
}