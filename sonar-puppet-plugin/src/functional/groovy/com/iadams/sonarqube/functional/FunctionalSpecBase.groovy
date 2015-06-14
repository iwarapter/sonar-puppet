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
import com.google.common.io.Files
import groovy.util.logging.Slf4j
import groovyx.net.http.HTTPBuilder
import org.apache.commons.io.FileUtils
import spock.lang.Specification

/**
 * @author iwarapter
 */
@Slf4j
//TODO This should probably all be pulled out into a library.
abstract class FunctionalSpecBase extends Specification {

	@TempDirectory(clean=false) protected File projectDir

	protected String moduleName
	protected File sonarProjectFile
	protected File analysisLog
	protected int returnCode

	protected static boolean didSonarStart

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
		sonarProjectFile << "sonar.scm.disabled=true\n"

		println "Running test from ${projectDir.getAbsolutePath()}"
	}

	def setupSpec(){
		println "Setup Spec"
		if(!isWebuiUp()){
			String sonarHome = System.getenv('SONARHOME')
			println "SONARHOME: $sonarHome"
			if(sonarHome){
				if(new File(sonarHome).exists()){
					cleanServerLog(sonarHome)
					installPlugin(sonarHome)
					assert startSonar(sonarHome) : "Cannot start SonarQube from $sonarHome exiting."
					didSonarStart = true
					checkServerLogs(sonarHome)
				}
				else {
					throw new FunctionalSpecException("The folder " + sonarHome + " does not exist.")
				}
			}
		}
		else {
			println "SonarQube is already running."
		}
	}

	def cleanupSpec(){
		if(didSonarStart){
			String sonarHome = System.getenv('SONARHOME')
			stopSonar(sonarHome)
		}
	}

	private String findModuleName() {
		projectDir.getName().replaceAll(/_\d+/, '')
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


	void theFollowingMetricsHaveTheFollowingValue(Map<String, Float> metrics_to_query){
		SonarApiUtils.queryMetrics('http://localhost:9000', moduleName, metrics_to_query.sort())
	}

	private static final String SONAR_ERROR = ".* ERROR .*"
	private static final String SONAR_WARN = ".* WARN .*"
	private static final String SONAR_WARN_TO_IGNORE_RE = ".*H2 database should.*|.*Starting search|.*Starting web|.*WEB DEVELOPMENT MODE IS ENABLED.*"

	public int errors = 0
	public int warnings = 0
	static def badLines = []

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
		return line.matches(SONAR_ERROR)
	}

	boolean isSonarWarning(String line){
		return line.matches(SONAR_WARN) && !line.matches(SONAR_WARN_TO_IGNORE_RE)
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

	private static String LOG_FILE_PATH = 'logs/sonar.log'
	private static String PLUGIN_DIR = 'extensions/plugins/'
	private static String SONAR_URL = 'http://localhost:9000'
	private static File JAR_PATH = new File('build/libs')

	boolean isInstalled(String sonarHome){
		new File(sonarHome).exists()
	}

	def cleanServerLog(String sonarHome){
		println "Removing ${sonarlog(sonarHome).absoluteFile}"
		sonarlog(sonarHome).delete()
	}

	def installPlugin(String sonarHome){
		println "Installing Plugin"

		File pluginPath = new File(sonarHome, PLUGIN_DIR)
		for(File path in pluginPath.listFiles()){
			if(path.isFile() && path=~/sonar-puppet-plugin-.*.jar/) {
				println "Removing ${path.name}"
				path.delete()
			}
		}
		def myFile = JAR_PATH.listFiles().find{it.isFile() && it=~/sonar-puppet-plugin-[0-9.]*.jar/}
		Files.copy(myFile, new File(pluginPath, myFile.name))
	}

	def startSonar(String sonarHome){
		println "Starting SonarQube"
		def cmd = startScript(sonarHome).execute()
		cmd.waitFor()
		cmd.exitValue() == 0
		assert waitForSonar(50)
		println "SonarQube Started"
		return true
	}

	def stopSonar(String sonarHome){
		println "Stopping SonarQube"
		def cmd = stopScript(sonarHome).execute()
		cmd.waitFor()
		cmd.exitValue() == 0
		assert waitForSonarDown(300)
		println "SonarQube Stopped"
		return true
	}

	def startScript(String sonarHome){
		return "$sonarHome/${scriptPath()} start"
	}

	def stopScript(String sonarHome){
		return "$sonarHome/${scriptPath()} stop"
	}

	def scriptPath(){
		//Just mac/linux atm
		if( System.getProperty("os.name") == "Mac OS X" && System.getProperty("os.arch") == "x86_64"){
			return "bin/macosx-universal-64/sonar.sh"
		}
		if( System.getProperty("os.name") == "Linux" && System.getProperty("os.arch") == "x86_64"){
			return "bin/linux-x86-64/sonar.sh"
		}
		return "bin/linux-x86-32/sonar.sh"
	}

	def sonarlog(String sonarHome){
		return new File(sonarHome, LOG_FILE_PATH)
	}

	def waitForSonar(int timeout){
		for (i in 0..timeout){
			if(isWebuiUp()){
				return true
			}
			sleep(1000)
		}
		return false
	}

	def waitForSonarDown(int timeout){
		for (i in timeout){
			if(isWebuiDown()){
				return true
			}
			sleep(1000)
		}
		return false
	}


	boolean isWebuiUp(){
		try {
			new HTTPBuilder(SONAR_URL).get( path:'') { response ->
				response.statusLine.statusCode == 200
			}
		}
		catch( e ) { false }
	}

	boolean isWebuiDown(){
		try {
			new HTTPBuilder(SONAR_URL).get( path:'') { response ->
				response.statusLine.statusCode == 200
				return false
			}
		}
		catch( e ) { true }
	}

	def checkServerLogs(String sonarHome){
		analyseLog(sonarlog(sonarHome))
		assert badLines.isEmpty() : ("Found following errors and/or warnings lines in the logfile:\n"
				+ badLines.join("\n")
				+ "For details see $analysisLog")
	}
}