package com.iadams.sonarqube.puppet

import com.google.common.base.Charsets;
import spock.lang.Specification;

/**
 * Created by iwarapter
 */
public class PuppetConfigurationModelSpec extends Specification {

	def "get configuration charset"(){
		given:
		PuppetConfigurationModel model = new PuppetConfigurationModel()

		when:
		model.charsetProperty.setValue("UTF-8")

		then:
		model.getCharset().equals(Charsets.UTF_8)
		model.getConfiuration().getCharset().equals(Charsets.UTF_8)

		when:
		model.charsetProperty.setValue("ISO-8859-1")

		then:
		model.getCharset().equals(Charsets.ISO_8859_1)
		model.getConfiuration().getCharset().equals(Charsets.ISO_8859_1)
	}

	def "get property or default value with property set"(){
		given:
		String oldValue = System.getProperty('foo')

		when:
		System.setProperty('foo', 'bar')

		then:
		PuppetConfigurationModel.getPropertyOrDefaultValue('foo', 'baz').equals('bar')
	}
}
