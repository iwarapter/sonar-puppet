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
package com.iadams.sonarqube.puppet;

import com.google.common.base.Preconditions;
import com.sonar.sslr.api.AstNode;

import java.lang.annotation.Annotation;
import javax.annotation.Nullable;

import org.sonar.api.utils.AnnotationUtils;
import org.sonar.squidbridge.annotations.SqaleLinearRemediation;
import org.sonar.squidbridge.annotations.SqaleLinearWithOffsetRemediation;
import org.sonar.squidbridge.api.CheckMessage;
import org.sonar.squidbridge.api.CodeCheck;
import org.sonar.squidbridge.api.SourceFile;
import org.sonar.squidbridge.checks.SquidCheck;

public class PuppetCheckVisitor extends SquidCheck {

  public void addIssue(AstNode node, CodeCheck check, String message) {
    addIssue(node.getTokenLine(), check, message, null);
  }

  public void addIssue(AstNode node, CodeCheck check, String message, Double cost) {
    addIssue(node.getTokenLine(), check, message, cost);
  }

  public void addIssue(int line, CodeCheck check, String message) {
    addIssue(line, check, message, null);
  }

  public void addIssueOnFile(CodeCheck check, String message) {
    addIssue(-1, check, message, null);
  }

  public void addIssue(@Nullable Integer line, CodeCheck check, String message, @Nullable Double cost) {
    Preconditions.checkNotNull(check);
    Preconditions.checkNotNull(message);
    CheckMessage checkMessage = new CheckMessage(check, message);
    if (line > 0) {
      checkMessage.setLine(line);
    }
    if (cost == null) {
      Annotation linear = AnnotationUtils.getAnnotation(check, SqaleLinearRemediation.class);
      Annotation linearWithOffset = AnnotationUtils.getAnnotation(check, SqaleLinearWithOffsetRemediation.class);
      if (linear != null || linearWithOffset != null) {
        throw new IllegalStateException("A check annotated with a linear SQALE function should provide an effort to fix.");
      }
    } else {
      checkMessage.setCost(cost);
    }

    if (getContext().peekSourceCode() instanceof SourceFile) {
      getContext().peekSourceCode().log(checkMessage);
    } else if (getContext().peekSourceCode().getParent(SourceFile.class) != null) {
      getContext().peekSourceCode().getParent(SourceFile.class).log(checkMessage);
    } else {
      throw new IllegalStateException("Unable to log a check message on source code '"
        + (getContext().peekSourceCode() == null ? "[NULL]" : getContext().peekSourceCode().getKey()) + "'");
    }
  }

}
