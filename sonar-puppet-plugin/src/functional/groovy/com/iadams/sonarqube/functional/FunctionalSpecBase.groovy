/*
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
package com.iadams.sonarqube.functional

import com.energizedwork.spock.extensions.TempDirectory
import groovy.util.logging.Slf4j
import org.apache.commons.io.FileUtils
import spock.lang.Specification

/**
 * @author iwarapter
 */
@Slf4j
abstract class FunctionalSpecBase extends Specification {

	@TempDirectory(clean=false) protected File projectDir

	protected String moduleName
	protected File sonarProjectFile
	protected File analysisLog
	protected int returnCode

	private String findModuleName() {
		projectDir.getName().replaceAll(/_\d+/, '')
	}

	def setup() {
		moduleName = findModuleName()
		analysisLog = new File(projectDir, moduleName + "-analysis.log")

		if (!sonarProjectFile) {
			sonarProjectFile = new File(projectDir, 'sonar-project.properties')
		}
		sonarProjectFile << "sonar.projectKey=$moduleName\n"
		sonarProjectFile << "sonar.projectName=$moduleName\n"
		sonarProjectFile << "sonar.projectVersion=1.0\n"
		sonarProjectFile << "sonar.sources=.\n"

		println "Running test from ${projectDir.getAbsolutePath()}"
	}

	def runSonarRunner(String args = "") {
		def cmd = "sonar-runner $args".execute(null, projectDir)
		cmd.waitFor()
		log.error(cmd.err.text)
		def output = cmd.text
		println output
		analysisLog.append(output)
		returnCode = cmd.exitValue()
	}

	def runSonarRunnerWithArguements(String args){
		runSonarRunner(args)
	}

	void analysisFinishesSuccessfully() {
		assert returnCode == 0 : "Exit code is $returnCode, but should be zero."
	}

	void analysisFails(){
		assert returnCode != 0 : "Exit code is $returnCode, but should be non-zero."
	}

	void analysisLogContainsNoErrorsOrWarnings() {
		analyseLog(analysisLog)
		assert badLines.size() == 0 : ("Found following errors and/or warnings lines in the logfile:\n"
				+ badLines.join("\n")
				+ "For details see $analysisLog")
	}

	void analysisLogContains(String line){
		assert analysisLog.text.contains(line)
	}

	private final String SONAR_ERROR = ".* ERROR .*"
	private final String SONAR_WARN = ".* WARN .*"
	private final String SONAR_WARN_TO_IGNORE_RE = ".*H2 database should.*|.*Starting search|.*Starting web"

	public int errors = 0
	public int warnings = 0
	def badLines = []

	def analyseLog(File logpath){
		logpath.eachLine {
			if(isSonarError(it)){
				errors++
				badLines.add(it)
			}

			if(isSonarWarning(it)){
				warnings++
				badLines.add(it)
			}
		}
	}

	boolean isSonarError(String line){
		return line.contains(SONAR_ERROR)
	}

	boolean isSonarWarning(String line){
		return line.contains(SONAR_WARN) && !line.contains(SONAR_WARN_TO_IGNORE_RE)
	}

	protected File directory(String path, File baseDir = projectDir) {
		new File(baseDir, path).with {
			mkdirs()
			it
		}
	}

	protected File file(String path, File baseDir = projectDir) {
		def splitted = path.split('/')
		def directory = splitted.size() > 1 ? directory(splitted[0..-2].join('/'), baseDir) : baseDir
		def file = new File(directory, splitted[-1])
		file.createNewFile()
		file
	}

	protected void copyResources(String srcDir, String destination) {
		ClassLoader classLoader = getClass().getClassLoader();
		URL resource = classLoader.getResource(srcDir);
		if (resource == null) {
			throw new RuntimeException("Could not find classpath resource: $srcDir")
		}

		File destinationFile = file(destination)
		File resourceFile = new File(resource.toURI())
		if (resourceFile.file) {
			FileUtils.copyFile(resourceFile, destinationFile)
		} else {
			FileUtils.copyDirectory(resourceFile, destinationFile)
		}
	}
}