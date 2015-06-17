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
package com.iadams.sonarqube.puppet.checks;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.squidbridge.annotations.NoSqale;
import org.sonar.squidbridge.annotations.RuleTemplate;
import org.sonar.squidbridge.checks.AbstractXPathCheck;

/**
 * @author iwarapter
 */
@Rule(
		key = XPathCheck.CHECK_KEY,
		priority = Priority.MAJOR,
		name = "XPath rule"
)
@NoSqale
@RuleTemplate
public class XPathCheck extends AbstractXPathCheck<Grammar> {
	public static final String CHECK_KEY = "XPath";
	private static final String DEFAULT_XPATH_QUERY = "";
	private static final String DEFAULT_MESSAGE = "The XPath expression matches this piece of code";

	@RuleProperty(
			key = "xpathQuery",
			defaultValue = "" + DEFAULT_XPATH_QUERY)
	public String xpathQuery = DEFAULT_XPATH_QUERY;

	@RuleProperty(
			key = "message",
			defaultValue = "" + DEFAULT_MESSAGE)
	public String message = DEFAULT_MESSAGE;

	@Override
	public String getXPathQuery() {
		return xpathQuery;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public void visitFile(AstNode fileNode) {
		if (fileNode != null) {
			super.visitFile(fileNode);
		}
	}

}