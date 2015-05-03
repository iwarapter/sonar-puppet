package com.iadams.sonarqube.puppet;

import com.sonar.sslr.api.AstAndTokenVisitor;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Token;
import org.sonar.squidbridge.SquidAstVisitor;
import org.sonar.squidbridge.measures.MetricDef;

import static com.sonar.sslr.api.GenericTokenType.EOF;

/**
 * Created by iwarapter
 */
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
