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
package com.iadams.sonarqube.puppet.checks;

import com.google.common.io.Closeables;
import com.sonar.sslr.api.AstNode;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.utils.SonarException;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;
import org.sonar.squidbridge.checks.SquidCheck;
import org.sonar.sslr.parser.LexerlessGrammar;

import java.io.IOException;
import java.io.RandomAccessFile;

@Rule(
		key = "EmptyLineEndOfFile",
		name = "Files should contain an empty new line at the end",
		priority = Priority.MINOR,
		tags = {Tags.CONVENTION})
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.READABILITY)
@SqaleConstantRemediation("1min")
@ActivatedByDefault
public class MissingNewLineAtEndOfFileCheck extends SquidCheck<LexerlessGrammar> {

	@Override
	public void visitFile(AstNode astNode) {
		RandomAccessFile randomAccessFile = null;
		try {
			randomAccessFile = new RandomAccessFile(getContext().getFile(), "r");
			if (!endsWithNewline(randomAccessFile)) {
				getContext().createFileViolation(this, "Add an empty new line at the end of this file.");
			}
		} catch (IOException e) {
			throw new SonarException(e);
		} finally {
			Closeables.closeQuietly(randomAccessFile);
		}
	}

	private static boolean endsWithNewline(RandomAccessFile randomAccessFile) throws IOException {
		if (randomAccessFile.length() < 1) {
			return false;
		}
		randomAccessFile.seek(randomAccessFile.length() - 1);
		byte[] chars = new byte[1];
		if (randomAccessFile.read(chars) < 1) {
			return false;
		}
		String ch = new String(chars);
		return "\n".equals(ch) || "\r".equals(ch);
	}

}
