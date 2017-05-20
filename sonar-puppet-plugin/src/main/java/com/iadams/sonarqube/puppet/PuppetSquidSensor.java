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
import com.iadams.sonarqube.puppet.metrics.FileLinesVisitor;
import com.sonar.sslr.api.Grammar;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.rule.Checks;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.issue.NoSonarFilter;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.FileLinesContextFactory;
import org.sonar.api.measures.Metric;
import org.sonar.api.rule.RuleKey;
import org.sonar.squidbridge.AstScanner;
import org.sonar.squidbridge.SquidAstVisitor;
import org.sonar.squidbridge.api.CheckMessage;
import org.sonar.squidbridge.api.SourceCode;
import org.sonar.squidbridge.api.SourceFile;
import org.sonar.squidbridge.indexer.QueryByType;

public final class PuppetSquidSensor implements Sensor {

  private static final Logger LOGGER = LoggerFactory.getLogger(PuppetSquidSensor.class);

  private final Checks<SquidAstVisitor<Grammar>> checks;
  private final FileLinesContextFactory fileLinesContextFactory;
  private final NoSonarFilter noSonarFilter;

  private SensorContext context;
  private AstScanner<Grammar> scanner;

  public PuppetSquidSensor(FileLinesContextFactory fileLinesContextFactory, CheckFactory checkFactory, NoSonarFilter noSonarFilter) {
    this.checks = checkFactory
      .<SquidAstVisitor<Grammar>>create(CheckList.REPOSITORY_KEY)
      .addAnnotatedChecks(CheckList.getChecks());
    this.fileLinesContextFactory = fileLinesContextFactory;
    this.noSonarFilter = noSonarFilter;
  }

  @Override
  public void describe(SensorDescriptor descriptor) {
    descriptor
      .onlyOnLanguage(Puppet.KEY)
      .name("Puppet Squid Sensor")
      .onlyOnFileType(InputFile.Type.MAIN);
  }

  @Override
  public void execute(SensorContext context) {
    this.context = context;
    Map<InputFile, Set<Integer>> linesOfCode = new HashMap<>();

    PuppetConfiguration conf = createConfiguration();

    List<SquidAstVisitor<Grammar>> visitors = Lists.newArrayList(checks.all());
    visitors.add(new FileLinesVisitor(fileLinesContextFactory, context.fileSystem(), linesOfCode, conf.getIgnoreHeaderComments()));
    visitors.add(new PuppetHighlighter(context));
    scanner = PuppetAstScanner.create(conf, visitors.toArray(new SquidAstVisitor[visitors.size()]));
    FilePredicates p = context.fileSystem().predicates();
    scanner.scanFiles(Lists.newArrayList(context.fileSystem().files(p.and(p.hasType(InputFile.Type.MAIN), p.hasLanguage(Puppet.KEY)))));

    Collection<SourceCode> squidSourceFiles = scanner.getIndex().search(new QueryByType(SourceFile.class));
    save(squidSourceFiles);
    new ProjectChecks(context).reportProjectIssues();
//    savePreciseIssues(
//      visitors
//        .stream()
//        .filter(v -> v instanceof PuppetCheck)
//        .map(v -> (PuppetCheck) v)
//        .collect(Collectors.toList()));

//    (new PuppetCoverageSensor()).execute(context, linesOfCode);
  }

  private PuppetConfiguration createConfiguration() {
    return new PuppetConfiguration(context.fileSystem().encoding());
  }

  private void save(Collection<SourceCode> squidSourceFiles) {
    for (SourceCode squidSourceFile : squidSourceFiles) {
      SourceFile squidFile = (SourceFile) squidSourceFile;

      InputFile inputFile = context.fileSystem().inputFile(context.fileSystem().predicates().is(new java.io.File(squidFile.getKey())));

      noSonarFilter.noSonarInFile(inputFile, squidFile.getNoSonarTagLines());

      saveMeasures(inputFile, squidFile);
      saveIssues(inputFile, squidFile);
    }
  }

  private void saveMeasures(InputFile sonarFile, SourceFile squidFile) {
    saveMetricOnFile(sonarFile, CoreMetrics.FILES, squidFile.getInt(PuppetMetric.FILES));
    saveMetricOnFile(sonarFile, CoreMetrics.LINES, squidFile.getInt(PuppetMetric.LINES));
    saveMetricOnFile(sonarFile, CoreMetrics.NCLOC, squidFile.getInt(PuppetMetric.LINES_OF_CODE));
    saveMetricOnFile(sonarFile, CoreMetrics.STATEMENTS, squidFile.getInt(PuppetMetric.STATEMENTS));
    saveMetricOnFile(sonarFile, CoreMetrics.FUNCTIONS, squidFile.getInt(PuppetMetric.FUNCTIONS));
    saveMetricOnFile(sonarFile, CoreMetrics.CLASSES, squidFile.getInt(PuppetMetric.CLASSES));
    saveMetricOnFile(sonarFile, CoreMetrics.COMPLEXITY, squidFile.getInt(PuppetMetric.COMPLEXITY));
    saveMetricOnFile(sonarFile, CoreMetrics.COMMENT_LINES, squidFile.getInt(PuppetMetric.COMMENT_LINES));
  }

  private <T extends Serializable> void saveMetricOnFile(InputFile inputFile, Metric metric, T value) {
    context.<T>newMeasure()
      .withValue(value)
      .forMetric(metric)
      .on(inputFile)
      .save();
  }

  private void saveIssues(InputFile inputFile, SourceFile squidFile) {
    Collection<CheckMessage> messages = squidFile.getCheckMessages();
    for (CheckMessage message : messages) {
      RuleKey ruleKey = checks.ruleKey((SquidAstVisitor<Grammar>) message.getCheck());
      NewIssue newIssue = context.newIssue();

      NewIssueLocation primaryLocation = newIssue.newLocation()
        .message(message.getText(Locale.ENGLISH))
        .on(inputFile);

      if (message.getLine() != null) {
        primaryLocation.at(inputFile.selectLine(message.getLine()));
      }

      newIssue.forRule(ruleKey).at(primaryLocation).save();
    }
  }
}
