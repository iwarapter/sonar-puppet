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
package com.iadams.sonarqube.puppet.checks

import com.iadams.sonarqube.puppet.PuppetAstScanner
import org.sonar.squidbridge.api.SourceFile
import org.sonar.squidbridge.checks.CheckMessagesVerifier
import spock.lang.Specification

class CommentRegularExpressionCheckSpec extends Specification {

	private final CommentRegularExpressionCheck check = new CommentRegularExpressionCheck();

	def "should contain some comments matching WTF"() {
		given:
		String message = "Stop annotating lines with WTF! Detail what is wrong instead.";
		check.regularExpression = "(?i).*WTF.*";
		check.message = message;
		SourceFile file = PuppetAstScanner.scanSingleFile(new File("src/test/resources/checks/CommentRegularExpression.pp"), check);

		expect:
		CheckMessagesVerifier.verify(file.getCheckMessages())
				.next().atLine(1).withMessage(message)
				.next().atLine(2).withMessage(message)
				.next().atLine(3).withMessage(message)
				.next().atLine(5).withMessage(message)
				.next().atLine(6).withMessage(message)
				.next().atLine(7).withMessage(message)
				.next().atLine(9).withMessage(message)
				.next().atLine(10).withMessage(message)
				.next().atLine(11).withMessage(message)
				.noMore();
	}

	def "should not contain any comments matching blabla"() {
		given:
		check.regularExpression = "(?i).*blabla.*";
		check.message = "Stop annotating lines with blabla. Detail what is wrong instead.";
		SourceFile file = PuppetAstScanner.scanSingleFile(new File("src/test/resources/checks/CommentRegularExpression.pp"), check);

		expect:
		CheckMessagesVerifier.verify(file.getCheckMessages()).noMore();
	}

}
