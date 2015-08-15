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

import com.google.common.base.Charsets;
import com.iadams.sonarqube.puppet.api.PuppetGrammar;
import com.iadams.sonarqube.puppet.api.PuppetMetric;
import com.iadams.sonarqube.puppet.metrics.ComplexityVisitor;
import com.iadams.sonarqube.puppet.metrics.FunctionVisitor;
import com.iadams.sonarqube.puppet.parser.PuppetParser;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.impl.Parser;

import java.io.File;
import java.util.Collection;

import org.sonar.squidbridge.AstScanner;
import org.sonar.squidbridge.SquidAstVisitor;
import org.sonar.squidbridge.SquidAstVisitorContextImpl;
import org.sonar.squidbridge.api.SourceCode;
import org.sonar.squidbridge.api.SourceFile;
import org.sonar.squidbridge.api.SourceProject;
import org.sonar.squidbridge.indexer.QueryByType;
import org.sonar.squidbridge.metrics.CommentsVisitor;
import org.sonar.squidbridge.metrics.CounterVisitor;
import org.sonar.squidbridge.metrics.LinesVisitor;

public class PuppetAstScanner {

  private PuppetAstScanner() {
  }

  /*
   * Helper method for testing checks without having to deploy them on a Sonar instance.
   */
  public static SourceFile scanSingleFile(File file, SquidAstVisitor<Grammar>... visitors) {
    if (!file.isFile()) {
      throw new IllegalArgumentException("File '" + file + "' not found.");
    }
    AstScanner<Grammar> scanner = create(new PuppetConfiguration(Charsets.UTF_8), visitors);
    scanner.scanFile(file);
    Collection<SourceCode> sources = scanner.getIndex().search(new QueryByType(SourceFile.class));
    if (sources.size() != 1) {
      throw new IllegalStateException("Only one SourceFile was expected whereas " + sources.size() + " has been returned.");
    }
    return (SourceFile) sources.iterator().next();
  }

  public static AstScanner<Grammar> create(PuppetConfiguration conf, SquidAstVisitor<Grammar>... visitors) {
    final SquidAstVisitorContextImpl<Grammar> context = new SquidAstVisitorContextImpl<Grammar>(new SourceProject("Puppet Project"));
    final Parser<Grammar> parser = PuppetParser.create(conf);

    AstScanner.Builder<Grammar> builder = AstScanner.<Grammar>builder(context).setBaseParser(parser);

    builder.withMetrics(PuppetMetric.values());
    builder.setFilesMetric(PuppetMetric.FILES);
    builder.setCommentAnalyser(new PuppetCommentAnalyser());

    builder.withSquidAstVisitor(new LinesVisitor<Grammar>(PuppetMetric.LINES));
    builder.withSquidAstVisitor(new PuppetLinesOfCodeVisitor<Grammar>(PuppetMetric.LINES_OF_CODE));
    builder.withSquidAstVisitor(new FunctionVisitor());
    builder.withSquidAstVisitor(new ComplexityVisitor());

    builder.withSquidAstVisitor(CommentsVisitor.<Grammar>builder().withCommentMetric(PuppetMetric.COMMENT_LINES)
      .withNoSonar(true)
      .withIgnoreHeaderComment(conf.getIgnoreHeaderComments())
      .build());

    builder.withSquidAstVisitor(CounterVisitor.<Grammar>builder()
      .setMetricDef(PuppetMetric.CLASSES)
      .subscribeTo(PuppetGrammar.CLASSDEF, PuppetGrammar.DEFINITION)
      .build());

    // TODO: To be discussed with the mapping of FUNCTIONS
    builder.withSquidAstVisitor(CounterVisitor.<Grammar>builder()
      .setMetricDef(PuppetMetric.RESOURCES)
      .subscribeTo(PuppetGrammar.RESOURCE)
      .build());

    builder.withSquidAstVisitor(CounterVisitor.<Grammar>builder()
      .setMetricDef(PuppetMetric.STATEMENTS)
      .subscribeTo(PuppetGrammar.STATEMENT)
      .build());

    for (SquidAstVisitor<Grammar> visitor : visitors) {
      if (visitor instanceof CharsetAwareVisitor) {
        ((CharsetAwareVisitor) visitor).setCharset(conf.getCharset());
      }
      builder.withSquidAstVisitor(visitor);
    }

    return builder.build();
  }

}
