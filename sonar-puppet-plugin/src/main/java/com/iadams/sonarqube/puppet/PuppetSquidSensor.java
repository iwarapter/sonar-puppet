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
package com.iadams.sonarqube.puppet;

import com.google.common.collect.Lists;
import com.iadams.sonarqube.puppet.api.PuppetMetric;
import com.iadams.sonarqube.puppet.checks.CheckList;
import com.iadams.sonarqube.puppet.checks.ProjectChecks;
import com.iadams.sonarqube.puppet.highlighter.PuppetHighlighter;
import com.iadams.sonarqube.puppet.metrics.FileLinesVisitor;
import com.iadams.sonarqube.puppet.metrics.PuppetLanguageMetrics;
import com.sonar.sslr.api.Grammar;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.rule.Checks;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.issue.Issuable;
import org.sonar.api.issue.Issue;
import org.sonar.api.issue.NoSonarFilter;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.FileLinesContextFactory;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.source.Highlightable;
import org.sonar.squidbridge.AstScanner;
import org.sonar.squidbridge.SquidAstVisitor;
import org.sonar.squidbridge.api.CheckMessage;
import org.sonar.squidbridge.api.SourceCode;
import org.sonar.squidbridge.api.SourceFile;
import org.sonar.squidbridge.indexer.QueryByType;

public class PuppetSquidSensor implements Sensor {

  private static final Logger LOGGER = LoggerFactory.getLogger(PuppetSquidSensor.class);

  private final Checks<SquidAstVisitor<Grammar>> checks;
  private final FileLinesContextFactory fileLinesContextFactory;
  private final FilePredicate mainFilePredicate;

  private SensorContext context;
  private FileSystem fileSystem;
  private ResourcePerspectives resourcePerspectives;
  private final NoSonarFilter noSonarFilter;
  private final RulesProfile rulesProfile;
  private Project project;

  public PuppetSquidSensor(FileLinesContextFactory fileLinesContextFactory, FileSystem fileSystem, ResourcePerspectives perspectives, CheckFactory checkFactory,
    NoSonarFilter noSonarFilter, RulesProfile rulesProfile) {
    this.fileLinesContextFactory = fileLinesContextFactory;
    this.fileSystem = fileSystem;
    this.resourcePerspectives = perspectives;
    this.noSonarFilter = noSonarFilter;
    this.rulesProfile = rulesProfile;

    this.mainFilePredicate = fileSystem.predicates().and(
      fileSystem.predicates().hasType(InputFile.Type.MAIN),
      fileSystem.predicates().hasLanguage(Puppet.KEY));

    checks = checkFactory
      .<SquidAstVisitor<Grammar>>create(CheckList.REPOSITORY_KEY)
      .addAnnotatedChecks(CheckList.getChecks());
  }

  @Override
  public boolean shouldExecuteOnProject(Project project) {
    FilePredicates p = fileSystem.predicates();
    return fileSystem.hasFiles(p.and(p.hasType(InputFile.Type.MAIN), p.hasLanguage(Puppet.KEY)));
  }

  @Override
  public void analyse(Project project, SensorContext context) {
    this.project = project;
    this.context = context;

    List<SquidAstVisitor<Grammar>> visitors = Lists.newArrayList(checks.all());
    visitors.add(new FileLinesVisitor(fileLinesContextFactory, fileSystem));
    AstScanner<Grammar> scanner = PuppetAstScanner.create(createConfiguration(), visitors.toArray(new SquidAstVisitor[visitors.size()]));
    FilePredicates p = fileSystem.predicates();
    scanner.scanFiles(Lists.newArrayList(fileSystem.files(p.and(p.hasType(InputFile.Type.MAIN), p.hasLanguage(Puppet.KEY)))));

    Collection<SourceCode> squidSourceFiles = scanner.getIndex().search(new QueryByType(SourceFile.class));
    save(squidSourceFiles);
    highlight();
  }

  private PuppetConfiguration createConfiguration() {
    return new PuppetConfiguration(fileSystem.encoding());
  }

  private void save(Collection<SourceCode> squidSourceFiles) {
    for (SourceCode squidSourceFile : squidSourceFiles) {
      SourceFile squidFile = (SourceFile) squidSourceFile;
      InputFile sonarFile = fileSystem.inputFile(fileSystem.predicates().hasAbsolutePath(squidFile.getKey()));

      if (sonarFile != null) {
        noSonarFilter.addComponent(((DefaultInputFile) sonarFile).key(), squidFile.getNoSonarTagLines());
      }
      saveMeasures(sonarFile, squidFile);
      saveIssues(sonarFile, squidFile);
    }
    ProjectChecks projectChecks = new ProjectChecks(project, fileSystem, rulesProfile, checks, resourcePerspectives);
    projectChecks.reportProjectIssues();
  }

  private void saveMeasures(InputFile sonarFile, SourceFile squidFile) {
    context.saveMeasure(sonarFile, CoreMetrics.FILES, squidFile.getDouble(PuppetMetric.FILES));
    context.saveMeasure(sonarFile, CoreMetrics.LINES, squidFile.getDouble(PuppetMetric.LINES));
    context.saveMeasure(sonarFile, CoreMetrics.NCLOC, squidFile.getDouble(PuppetMetric.LINES_OF_CODE));
    context.saveMeasure(sonarFile, CoreMetrics.STATEMENTS, squidFile.getDouble(PuppetMetric.STATEMENTS));
    context.saveMeasure(sonarFile, CoreMetrics.FUNCTIONS, squidFile.getDouble(PuppetMetric.FUNCTIONS));
    context.saveMeasure(sonarFile, CoreMetrics.CLASSES, squidFile.getDouble(PuppetMetric.CLASSES));
    context.saveMeasure(sonarFile, CoreMetrics.COMPLEXITY, squidFile.getDouble(PuppetMetric.COMPLEXITY));
    context.saveMeasure(sonarFile, CoreMetrics.COMMENT_LINES, squidFile.getDouble(PuppetMetric.COMMENT_LINES));
    context.saveMeasure(sonarFile, PuppetLanguageMetrics.PUPPET_RESOURCES, squidFile.getDouble(PuppetMetric.RESOURCES));
  }

  private void saveIssues(InputFile sonarFile, SourceFile squidFile) {
    Collection<CheckMessage> messages = squidFile.getCheckMessages();
    for (CheckMessage message : messages) {
      RuleKey ruleKey = checks.ruleKey((SquidAstVisitor<Grammar>) message.getCheck());
      Issuable issuable = resourcePerspectives.as(Issuable.class, sonarFile);

      if (issuable != null) {
        Issue issue = issuable.newIssueBuilder()
          .ruleKey(ruleKey)
          .line(message.getLine())
          .message(message.getText(Locale.ENGLISH))
          .effortToFix(message.getCost())
          .build();
        issuable.addIssue(issue);
      }
    }
  }

  private void highlight() {
    PuppetHighlighter highlighter = new PuppetHighlighter(createConfiguration());

    for (InputFile inputFile : fileSystem.inputFiles(mainFilePredicate)) {
      Highlightable perspective = resourcePerspectives.as(Highlightable.class, inputFile);

      if (perspective != null) {
        highlighter.highlight(perspective, inputFile.file());

      } else {
        LOGGER.warn("Could not get " + Highlightable.class.getCanonicalName() + " for " + inputFile.file());
      }
    }
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
