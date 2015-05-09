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
package com.iadams.sonarqube.puppet;

import com.google.common.collect.Lists;
import com.iadams.sonarqube.puppet.api.PuppetMetric;
import com.iadams.sonarqube.puppet.checks.CheckList;
import com.iadams.sonarqube.puppet.metrics.FileLinesVisitor;
import com.sonar.sslr.api.Grammar;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.checks.AnnotationCheckFactory;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.FileLinesContextFactory;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.File;
import org.sonar.api.resources.Project;
import org.sonar.api.scan.filesystem.FileQuery;
import org.sonar.api.scan.filesystem.ModuleFileSystem;
import org.sonar.squidbridge.AstScanner;
import org.sonar.squidbridge.SquidAstVisitor;
import org.sonar.squidbridge.api.SourceCode;
import org.sonar.squidbridge.api.SourceFile;
import org.sonar.squidbridge.indexer.QueryByType;

import java.util.Collection;
import java.util.List;

/**
 * Created by iwarapter
 */
public class PuppetSquidSensor implements Sensor {

	private static final Number[] FUNCTIONS_DISTRIB_BOTTOM_LIMITS = {1, 2, 4, 6, 8, 10, 12, 20, 30};
	private static final Number[] FILES_DISTRIB_BOTTOM_LIMITS = {0, 5, 10, 20, 30, 60, 90};

	private final AnnotationCheckFactory annotationCheckFactory;
	private final FileLinesContextFactory fileLinesContextFactory;

	private Project project;
	private SensorContext context;
	private AstScanner<Grammar> scanner;
	private ModuleFileSystem fileSystem;
	private ResourcePerspectives resourcePerspectives;

	public PuppetSquidSensor(RulesProfile profile, FileLinesContextFactory fileLinesContextFactory, ModuleFileSystem fileSystem, ResourcePerspectives resourcePerspectives) {
		this.annotationCheckFactory = AnnotationCheckFactory.create(profile, CheckList.REPOSITORY_KEY, CheckList.getChecks());
		this.fileLinesContextFactory = fileLinesContextFactory;
		this.fileSystem = fileSystem;
		this.resourcePerspectives = resourcePerspectives;
	}

	public boolean shouldExecuteOnProject(Project project) {
		return !fileSystem.files(FileQuery.onSource().onLanguage(Puppet.KEY)).isEmpty();
	}

	public void analyse(Project project, SensorContext context) {
		this.project = project;
		this.context = context;

		Collection<SquidAstVisitor<Grammar>> squidChecks = annotationCheckFactory.getChecks();
		List<SquidAstVisitor<Grammar>> visitors = Lists.newArrayList(squidChecks);
		visitors.add(new FileLinesVisitor(project, fileLinesContextFactory));
		this.scanner = PuppetAstScanner.create(createConfiguration(project), visitors.toArray(new SquidAstVisitor[visitors.size()]));
		scanner.scanFiles(fileSystem.files(FileQuery.onSource().onLanguage(Puppet.KEY)));

		Collection<SourceCode> squidSourceFiles = scanner.getIndex().search(new QueryByType(SourceFile.class));
		save(squidSourceFiles);
	}

	private PuppetConfiguration createConfiguration(Project project) {
		return new PuppetConfiguration(fileSystem.sourceCharset());
	}

	private void save(Collection<SourceCode> squidSourceFiles) {
		for (SourceCode squidSourceFile : squidSourceFiles) {
			SourceFile squidFile = (SourceFile) squidSourceFile;

			File sonarFile = File.fromIOFile(new java.io.File(squidFile.getKey()), project);

			//saveFilesComplexityDistribution(sonarFile, squidFile);
			//saveFunctionsComplexityDistribution(sonarFile, squidFile);
			saveMeasures(sonarFile, squidFile);
			//saveIssues(sonarFile, squidFile);
		}
	}

	private void saveMeasures(File sonarFile, SourceFile squidFile) {
		context.saveMeasure(sonarFile, CoreMetrics.FILES, squidFile.getDouble(PuppetMetric.FILES));
		context.saveMeasure(sonarFile, CoreMetrics.LINES, squidFile.getDouble(PuppetMetric.LINES));
		context.saveMeasure(sonarFile, CoreMetrics.NCLOC, squidFile.getDouble(PuppetMetric.LINES_OF_CODE));
		context.saveMeasure(sonarFile, CoreMetrics.STATEMENTS, squidFile.getDouble(PuppetMetric.STATEMENTS));
		context.saveMeasure(sonarFile, CoreMetrics.FUNCTIONS, squidFile.getDouble(PuppetMetric.FUNCTIONS));
		context.saveMeasure(sonarFile, CoreMetrics.CLASSES, squidFile.getDouble(PuppetMetric.CLASSES));
		context.saveMeasure(sonarFile, CoreMetrics.COMPLEXITY, squidFile.getDouble(PuppetMetric.COMPLEXITY));
		context.saveMeasure(sonarFile, CoreMetrics.COMMENT_LINES, squidFile.getDouble(PuppetMetric.COMMENT_LINES));
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
