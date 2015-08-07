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
package com.iadams.sonarqube.puppet.cpd;

import com.iadams.sonarqube.puppet.PuppetConfiguration;
import com.iadams.sonarqube.puppet.api.PuppetTokenType;
import com.iadams.sonarqube.puppet.lexer.PuppetLexer;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.Lexer;
import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.cpd.TokenEntry;
import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.cpd.Tokens;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

public class PuppetTokenizer implements Tokenizer {

	private final Charset charset;

	public PuppetTokenizer(Charset charset){
		this.charset = charset;
	}

	@Override
	public final void tokenize(SourceCode source, Tokens cpdTokens){
		Lexer lexer = PuppetLexer.create(new PuppetConfiguration(charset));
		String fileName = source.getFileName();
		List<Token> tokens = lexer.lex(new File(fileName));
		for(Token token : tokens){
			if (!token.getType().equals(PuppetTokenType.NEWLINE) && !token.getType().equals(PuppetTokenType.DEDENT) && !token.getType().equals(PuppetTokenType.INDENT)) {
				TokenEntry cpdToken = new TokenEntry(getTokenImage(token), fileName, token.getLine());
				cpdTokens.add(cpdToken);
			}
		}
		cpdTokens.add(TokenEntry.getEOF());
	}

	private String getTokenImage(Token token) {
		return token.getValue();
	}
}