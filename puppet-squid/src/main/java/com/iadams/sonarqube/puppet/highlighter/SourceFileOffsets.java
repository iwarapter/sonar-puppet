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
package com.iadams.sonarqube.puppet.highlighter;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.sonar.sslr.api.Token;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public class SourceFileOffsets {
  private final int length;
  private final List<Integer> lineStartOffsets = Lists.newArrayList();

  public SourceFileOffsets(String content) {
    this.length = content.length();
    initOffsets(content);
  }

  public SourceFileOffsets(File file, Charset charset) {
    this(fileContent(file, charset));
  }

  private static String fileContent(File file, Charset charset) {
    String fileContent;
    try {
      fileContent = Files.toString(file, charset);
    } catch (IOException e) {
      throw new IllegalStateException("Could not read " + file, e);
    }
    return fileContent;
  }

  private void initOffsets(String toParse) {
    lineStartOffsets.add(0);
    int i = 0;
    while (i < length) {
      if (toParse.charAt(i) == '\n' || toParse.charAt(i) == '\r') {
        int nextLineStartOffset = i + 1;
        if (i < (length - 1) && toParse.charAt(i) == '\r' && toParse.charAt(i + 1) == '\n') {
          nextLineStartOffset = i + 2;
          i++;
        }
        lineStartOffsets.add(nextLineStartOffset);
      }
      i++;
    }
  }

  public int startOffset(Token token) {
    int lineStartOffset = lineStartOffsets.get(token.getLine() - 1);
    int column = token.getColumn();
    return lineStartOffset + column;
  }

  public int endOffset(Token token) {
    return startOffset(token) + token.getValue().length();
  }
}
