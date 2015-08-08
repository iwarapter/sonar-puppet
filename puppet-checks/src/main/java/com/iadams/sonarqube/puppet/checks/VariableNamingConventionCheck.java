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
package com.iadams.sonarqube.puppet.checks;

import com.iadams.sonarqube.puppet.api.PuppetTokenType;
import com.sonar.sslr.api.AstNode;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;
import org.sonar.squidbridge.checks.SquidCheck;
import org.sonar.sslr.parser.LexerlessGrammar;

@Rule(
		key = "VariableNamingConvention",
		name = "Variables should follow a naming convention",
		priority = Priority.MINOR,
		tags = {Tags.CONVENTION})
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.READABILITY)
@SqaleConstantRemediation("5min")
@ActivatedByDefault
public class VariableNamingConventionCheck extends SquidCheck<LexerlessGrammar> {

	private static final String FORMAT = "^\\$(::)?([a-z0-9_]+::)*[a-z0-9_]+$";

	@Override
	public void init() {
		subscribeTo(PuppetTokenType.VARIABLE);
	}

	@Override
	public void leaveNode(AstNode node) {
		if (!node.getTokenValue().matches(FORMAT)) {
			getContext().createLineViolation(this, "Rename variable \"{0}\" to match the regular expression: ^\\$(::)?([a-z0-9_]+::)*[a-z0-9_]+$", node, node.getTokenValue().substring(1));
		}
	}

}
