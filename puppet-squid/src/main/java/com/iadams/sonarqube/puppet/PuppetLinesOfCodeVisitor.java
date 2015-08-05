/*
 * SonarQube Puppet Plugin
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
package com.iadams.sonarqube.puppet;

import com.sonar.sslr.api.AstAndTokenVisitor;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Token;
import org.sonar.squidbridge.SquidAstVisitor;
import org.sonar.squidbridge.measures.MetricDef;

import static com.sonar.sslr.api.GenericTokenType.EOF;

public class PuppetLinesOfCodeVisitor<GRAMMAR extends Grammar> extends SquidAstVisitor<GRAMMAR> implements AstAndTokenVisitor {

	private final MetricDef metric;
	private int lastTokenLine;

	public PuppetLinesOfCodeVisitor(MetricDef metric){
		this.metric = metric;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void	visitFile(AstNode node){
		lastTokenLine = -1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitToken(Token token){
		if (!token.getType().equals(EOF)){
			String[] tokenLines = token.getValue().split("\n", -1);

			int firstLineAlreadyCounted = lastTokenLine == token.getLine() ? 1 : 0;
			getContext().peekSourceCode().add(metric, (double)tokenLines.length - firstLineAlreadyCounted);

			lastTokenLine = token.getLine() + tokenLines.length - 1;
		}

	}
}
