package com.iadams.sonarqube.puppet;

import com.google.common.base.Charsets;
import com.iadams.sonarqube.puppet.api.PuppetMetric;
import com.iadams.sonarqube.puppet.parser.PuppetParser;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.impl.Parser;
import org.sonar.squidbridge.*;
import org.sonar.squidbridge.api.*;
import org.sonar.squidbridge.indexer.QueryByType;
import org.sonar.squidbridge.metrics.CommentsVisitor;
import org.sonar.squidbridge.metrics.LinesVisitor;

import java.io.File;
import java.util.Collection;

/**
 * Created by iwarapter
 */
public class PuppetAstScanner {

	private PuppetAstScanner(){}

	/**
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

    /* Metrics */
		builder.withMetrics(PuppetMetric.values());

    /* Files */
		builder.setFilesMetric(PuppetMetric.FILES);

    /* Comments */
		builder.setCommentAnalyser(new PuppetCommentAnalyser());

    /* Metrics */
		builder.withSquidAstVisitor(new LinesVisitor<Grammar>(PuppetMetric.LINES));
		builder.withSquidAstVisitor(new PuppetLinesOfCodeVisitor<Grammar>(PuppetMetric.LINES_OF_CODE));
		builder.withSquidAstVisitor(CommentsVisitor.<Grammar>builder().withCommentMetric(PuppetMetric.COMMENT_LINES)
				.withNoSonar(true)
				.withIgnoreHeaderComment(conf.getIgnoreHeaderComments())
				.build());


    /* External visitors (typically Check ones) */
		for (SquidAstVisitor<Grammar> visitor : visitors) {
			builder.withSquidAstVisitor(visitor);
		}

		return builder.build();
	}

}
