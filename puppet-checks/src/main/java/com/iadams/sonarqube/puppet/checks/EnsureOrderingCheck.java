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

import com.iadams.sonarqube.puppet.api.PuppetGrammar;
import com.sonar.sslr.api.*;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;
import org.sonar.squidbridge.checks.SquidCheck;

import java.util.ArrayList;
import java.util.List;

/**
 * @author iwarapter
 */
@Rule(
		key = EnsureOrderingCheck.CHECK_KEY,
		priority = Priority.MINOR,
		name = "Ensure attribute should be the first attribute specified",
		tags = {Tags.CONVENTION, Tags.CONFUSING}
)
@ActivatedByDefault
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.READABILITY)
@SqaleConstantRemediation("1min")
public class EnsureOrderingCheck extends SquidCheck<Grammar> {

	public static final String CHECK_KEY = "EnsureOrdering";

	@Override
	public void init() {
		subscribeTo(PuppetGrammar.RESOURCE);
	}

	@Override
	public void visitNode(AstNode node) {
		List<AstNode> attributes = new ArrayList<>();
		boolean containsEnsureAttribute = false;
		Integer id = null;
		for(AstNode n : node.getChildren()){
			if(n.getName().equals("ATTRIBUTE")){
				attributes.add(n);
				if(n.getToken().getValue().equals("ensure")){
					id = attributes.size()-1;
					containsEnsureAttribute = true;
				}
			}
		}
		if(containsEnsureAttribute){
			if(!attributes.get(0).getToken().getValue().equals("ensure")) {
				getContext().createLineViolation(this, "Ensure should be declared first.", attributes.get(id));
			}
		}

	}
}
