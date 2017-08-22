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

import com.iadams.sonarqube.puppet.PuppetCheckVisitor;
import com.iadams.sonarqube.puppet.api.PuppetTokenType;
import com.sonar.sslr.api.AstNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

@Rule(
  key = "PuppetURLModules",
  name = "\"puppet:///\" URL path should start with \"modules/\"",
  priority = Priority.CRITICAL,
  tags = {Tags.PITFALL})
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.INSTRUCTION_RELIABILITY)
@SqaleConstantRemediation("10min")
@ActivatedByDefault
public class PuppetURLModulesCheck extends PuppetCheckVisitor {

  private static final String PREFIX = "puppet:///";

  @RuleProperty(key = "mountPoints", description = "Comma-separated list of additional mount points in complement of 'modules' and variable usage.", defaultValue = "")
  public String mountPoints = "";

  @Override
  public void init() {
    subscribeTo(PuppetTokenType.DOUBLE_QUOTED_STRING_LITERAL, PuppetTokenType.SINGLE_QUOTED_STRING_LITERAL);
  }

  @Override
  public void visitNode(AstNode node) {
    if (node.getTokenValue().substring(1).startsWith(PREFIX)) {
      List<String> validPaths = new ArrayList<>(Arrays.asList(PREFIX + "modules/", PREFIX + "$"));
      if (StringUtils.isNotBlank(mountPoints)) {
        for (String pathCustom : mountPoints.split(",")) {
          if (StringUtils.isNotBlank(pathCustom)) {
            validPaths.add(PREFIX + pathCustom + "/");
          }
        }
      }
      if (!StringUtils.startsWithAny(node.getTokenValue().substring(1), validPaths.toArray(new String[validPaths.size()]))) {
        String message = "Add \"modules/\" to the path";
        if (validPaths.size() > 2) {
          message += " (Or " + mountPoints + ")";
        }
        message += ".";
        addIssue(node, this, message);
      }
    }
  }

}
