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

import com.google.common.collect.Lists;
import com.iadams.sonarqube.puppet.api.PuppetGrammar;
import com.sonar.sslr.api.AstNode;

import java.util.List;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;
import org.sonar.squidbridge.checks.SquidCheck;
import org.sonar.sslr.parser.LexerlessGrammar;

@Rule(
  key = "DuplicatedHashKeys",
  name = "Duplicated hash keys should be removed",
  priority = Priority.CRITICAL,
  tags = {Tags.BUG})
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.DATA_RELIABILITY)
@SqaleConstantRemediation("5min")
@ActivatedByDefault
public class DuplicatedHashKeysCheck extends SquidCheck<LexerlessGrammar> {

  private List<String> keys = Lists.newArrayList();

  @Override
  public void init() {
    subscribeTo(PuppetGrammar.HASH_PAIRS);
  }

  @Override
  public void visitNode(AstNode paramsNode) {
    keys.clear();
    for (AstNode hashPairNode : paramsNode.getChildren(PuppetGrammar.HASH_PAIR)) {
      if (keys.contains(hashPairNode.getFirstChild(PuppetGrammar.KEY).getTokenValue())) {
        getContext().createLineViolation(this,
          "Remove the duplicated key \"{0}\".",
          hashPairNode.getFirstChild(PuppetGrammar.KEY),
          hashPairNode.getFirstChild(PuppetGrammar.KEY).getTokenValue());
      } else {
        keys.add(hashPairNode.getFirstChild(PuppetGrammar.KEY).getTokenValue());
      }
    }
  }

}
