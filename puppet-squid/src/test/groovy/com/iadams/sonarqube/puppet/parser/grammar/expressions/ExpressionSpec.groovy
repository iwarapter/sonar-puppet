package com.iadams.sonarqube.puppet.parser.grammar.expressions

import com.iadams.sonarqube.puppet.parser.GrammarSpec
import spock.lang.Unroll

import static com.iadams.sonarqube.puppet.api.PuppetGrammar.EXPRESSION
import static org.sonar.sslr.tests.Assertions.assertThat

/**
 * Created by iwarapter
 */
class ExpressionSpec extends GrammarSpec {

	def setup() {
		setRootRule(EXPRESSION)
	}

	@Unroll
	def "\"#input\" parse correctly"() {
		expect:
		assertThat(p).matches(input)

		where:
		input << ['true == true',
				'5 < 9',
				'($operatingsystem != \'Solaris\')',
				//'$kernel in [\'linux\', \'solaris\']',
				'!str2bool($is_virtual)']
	}
}
