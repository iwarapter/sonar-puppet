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

import com.sonar.sslr.api.AstAndTokenVisitor;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Token;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;
import org.sonar.squidbridge.checks.SquidCheck;

/**
 * @author iwarapter
 */
@Rule(
		key = LineLengthCheck.CHECK_KEY,
		priority = Priority.MINOR,
		name = "Lines should not be too long",
		tags = Tags.CONVENTION
)
@ActivatedByDefault
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.READABILITY)
@SqaleConstantRemediation("1min")
public class LineLengthCheck extends SquidCheck<Grammar> implements AstAndTokenVisitor {

	public static final String CHECK_KEY = "LineLength";
	private static final int DEFAULT_MAXIMUM_LINE_LENHGTH = 80;

	@RuleProperty(
			key = "maximumLineLength",
			defaultValue = "" + DEFAULT_MAXIMUM_LINE_LENHGTH)
	public int maximumLineLength = DEFAULT_MAXIMUM_LINE_LENHGTH;

	public int getMaximumLineLength() {
		return maximumLineLength;
	}

	private Token previousToken;

	@Override
	public void visitFile(AstNode astNode) {
		previousToken = null;
	}

	@Override
	public void leaveFile(AstNode astNode) {
		previousToken = null;
	}

	@Override
	public void visitToken(Token token) {
		if (!token.isGeneratedCode()) {
			if (previousToken != null && previousToken.getLine() != token.getLine()) {
				// Note that AbstractLineLengthCheck doesn't support tokens which span multiple lines - see SONARPLUGINS-2025
				String[] lines = previousToken.getValue().split("\r?\n|\r", -1);
				int length = previousToken.getColumn();
				for (int line = 0; line < lines.length; line++) {
					length += lines[line].length();
					if (length > getMaximumLineLength()) {
						// Note that method from AbstractLineLengthCheck generates other message - see SONARPLUGINS-1809
						getContext().createLineViolation(this,
								"The line contains {0,number,integer} characters which is greater than {1,number,integer} authorized.",
								previousToken.getLine(),
								length,
								getMaximumLineLength());
					}
					length = 0;
				}
			}
			previousToken = token;
		}
	}
}
