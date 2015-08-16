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

import com.google.common.base.Joiner;
import com.iadams.sonarqube.puppet.PuppetCheckVisitor;
import com.iadams.sonarqube.puppet.api.PuppetGrammar;
import com.sonar.sslr.api.AstNode;

import java.util.Arrays;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

@Rule(
  key = "AutoLoaderLayout",
  priority = Priority.CRITICAL,
  name = "Manifest files should be in autoloader layout",
  tags = Tags.PITFALL)
@ActivatedByDefault
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.ARCHITECTURE_RELIABILITY)
@SqaleConstantRemediation("1h")
public class AutoLoaderLayoutCheck extends PuppetCheckVisitor {

  @Override
  public void init() {
    subscribeTo(PuppetGrammar.CLASSDEF, PuppetGrammar.DEFINITION);
  }

  @Override
  public void visitNode(AstNode astNode) {
    String name = astNode.getFirstChild(PuppetGrammar.CLASSNAME).getTokenValue();
    String[] splitName = name.split("::");
    String module = splitName[0];

    StringBuilder path = new StringBuilder();
    if (splitName.length > 1) {
      path.append('/').append(module).append("/manifests/")
        .append(Joiner.on('/').join(
          Arrays.copyOfRange(splitName, 1, splitName.length)))
        .append(".pp");
    }
    else {
      path.append('/').append(name).append("/manifests/init.pp");
    }

    if(!hasFullModulePath(getContext().getFile().getAbsolutePath())){
      path.replace(0,1,"");
    }

    if (!getContext().getFile().getAbsolutePath().endsWith(path.toString())) {
      addIssueOnFile(this, "\"" + getContext().getFile().getName() + "\" not in autoload module layout");
    }
  }

  private boolean hasFullModulePath(String path){
    String pathAfterModule = path.substring(path.lastIndexOf("modules/")+8);

    return pathAfterModule.substring(pathAfterModule.indexOf('/')).startsWith("/manifests/");
  }
}
