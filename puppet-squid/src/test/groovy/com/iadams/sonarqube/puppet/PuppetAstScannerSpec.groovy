package com.iadams.sonarqube.puppet

import com.google.common.base.Charsets
import com.google.common.collect.ImmutableList
import com.iadams.sonarqube.puppet.api.PuppetMetric
import com.sonar.sslr.api.Grammar
import org.sonar.squidbridge.AstScanner
import org.sonar.squidbridge.api.SourceFile
import org.sonar.squidbridge.api.SourceProject
import org.sonar.squidbridge.indexer.QueryByType
import spock.lang.Ignore
import spock.lang.Specification

/**
 * Created by iwarapter
 */
class PuppetAstScannerSpec extends Specification {

	def "files"() {
		given:
		AstScanner<Grammar> scanner = PuppetAstScanner.create(new PuppetConfiguration(Charsets.UTF_8))
		scanner.scanFiles(ImmutableList.of(new File("src/test/resources/metrics/lines_of_code.pp"), new File("src/test/resources/metrics/comments.pp")))
		SourceProject project = (SourceProject) scanner.getIndex().search(new QueryByType(SourceProject.class)).iterator().next()

		expect:
		project.getInt(PuppetMetric.FILES) == 2
	}

	def "comments"() {
		given:
		SourceFile file = PuppetAstScanner.scanSingleFile(new File("src/test/resources/metrics/comments.pp"))

		expect:
		file.getInt(PuppetMetric.COMMENT_LINES) == 1
		file.getNoSonarTagLines().contains(3)
	}

	@Ignore
	def "lines"() {
		given:
		SourceFile file = PuppetAstScanner.scanSingleFile(new File("src/test/resources/metrics/lines.pp"))

		expect:
		file.getInt(PuppetMetric.LINES) == 6
	}

	@Ignore
	def "lines of code"(){
		given:
		SourceFile file = PuppetAstScanner.scanSingleFile(new File("src/test/resources/metrics/lines_of_code.pp"))

		expect:
		file.getInt(PuppetMetric.LINES_OF_CODE) == 1
	}
}
