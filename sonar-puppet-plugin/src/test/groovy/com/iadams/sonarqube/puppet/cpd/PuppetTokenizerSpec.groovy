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
package com.iadams.sonarqube.puppet.cpd

import net.sourceforge.pmd.cpd.SourceCode
import net.sourceforge.pmd.cpd.TokenEntry
import net.sourceforge.pmd.cpd.Tokens
import spock.lang.Specification

import java.nio.charset.Charset

/**
 * @author iwarapter
 */
class PuppetTokenizerSpec extends Specification {

	def "should work on valid input"(){
		given:
		File file = new File(getClass().getResource("/com/iadams/sonarqube/puppet/code_chunks.pp").toURI());
		SourceCode source = new SourceCode(new SourceCode.FileCodeLoader(file, "key"))
		Tokens cpdTokens = new Tokens();
		PuppetTokenizer tokenizer = new PuppetTokenizer(Charset.forName("UTF-8"))
		tokenizer.tokenize(source, cpdTokens)

		expect:
		List<TokenEntry> list = cpdTokens.getTokens()
		list.size() == 20
	}
}
