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
package com.iadams.sonarqube.puppet.checks;

import com.google.common.io.Files;
import com.iadams.sonarqube.puppet.CharsetAwareVisitor;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import javax.annotation.Nullable;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.utils.SonarException;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;
import org.sonar.squidbridge.checks.SquidCheck;

@Rule(
  key = "LineLength",
  priority = Priority.MINOR,
  name = "Lines should not be too long",
  tags = Tags.CONVENTION)
@ActivatedByDefault
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.READABILITY)
@SqaleConstantRemediation("1min")
public class LineLengthCheck extends SquidCheck<Grammar> implements CharsetAwareVisitor {

  private static final int DEFAULT_MAXIMUM_LINE_LENGTH = 80;
  private Charset charset;

  @RuleProperty(
    key = "maximumLineLength",
    defaultValue = "" + DEFAULT_MAXIMUM_LINE_LENGTH)
  public int maximumLineLength = DEFAULT_MAXIMUM_LINE_LENGTH;

  @Override
  public void setCharset(Charset charset) {
    this.charset = charset;
  }

  @Override
  public void visitFile(@Nullable AstNode astNode) {
    List<String> lines;
    try {
      lines = Files.readLines(getContext().getFile(), charset);
    } catch (IOException e) {
      throw new SonarException(e);
    }
    for (int i = 0; i < lines.size(); i++) {
      String line = lines.get(i);
      if (line.length() > maximumLineLength) {
        getContext().createLineViolation(this,
          "The line contains {0,number,integer} characters which is greater than {1,number,integer} authorized.",
          i + 1,
          line.length(),
          maximumLineLength);
      }
    }
  }

}
