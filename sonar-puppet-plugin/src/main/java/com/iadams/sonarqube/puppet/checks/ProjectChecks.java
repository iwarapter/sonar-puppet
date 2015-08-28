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

import com.sonar.sslr.api.Grammar;

import java.io.File;

import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.rule.Checks;
import org.sonar.api.component.Component;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.issue.Issuable;
import org.sonar.api.issue.Issue;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Directory;
import org.sonar.api.resources.Project;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.rules.ActiveRule;
import org.sonar.squidbridge.SquidAstVisitor;
import org.sonar.squidbridge.api.CodeVisitor;

public class ProjectChecks {

  private final Project project;
  private final FileSystem fileSystem;
  private final RulesProfile rulesProfile;
  private final Checks<SquidAstVisitor<Grammar>> checks;
  private final ResourcePerspectives resourcePerspectives;

  public ProjectChecks(Project project, FileSystem fileSystem, RulesProfile rulesProfile, Checks<SquidAstVisitor<Grammar>> checks,
    ResourcePerspectives resourcePerspectives) {
    this.project = project;
    this.fileSystem = fileSystem;
    this.rulesProfile = rulesProfile;
    this.checks = checks;
    this.resourcePerspectives = resourcePerspectives;
  }

  public void reportProjectIssues() {
    if (fileSystem.baseDir() != null) {
      checkTestsDirectoryPresent(fileSystem.baseDir());
      checkMetadataJsonFilePresent(fileSystem.baseDir());
      checkReadmeFilePresent(fileSystem.baseDir());
    }
  }

  private void checkMetadataJsonFilePresent(File parentFile) {
    for (File file : parentFile.listFiles()) {
      if (file.isDirectory()) {
        if ("manifests".equals(file.getName())) {
          boolean metadataJsonFileFound = false;
          for (File testsSiblings : parentFile.listFiles()) {
            if (testsSiblings.isFile() && "metadata.json".equals(testsSiblings.getName())) {
              metadataJsonFileFound = true;
              break;
            }
          }
          if (!metadataJsonFileFound) {
            String path = Directory.fromIOFile(parentFile, project).getPath() != null ? Directory.fromIOFile(parentFile, project).getPath() : parentFile.getName();
            addIssue(MetadataJsonFilePresentCheck.RULE_KEY, "Add a \"metadata.json\" file to the \"" + path + "\" Puppet module.");
          }
        } else {
          checkMetadataJsonFilePresent(file);
        }
      }
    }
  }

  private void checkReadmeFilePresent(File parentFile) {
    for (File file : parentFile.listFiles()) {
      if (file.isDirectory()) {
        if ("manifests".equals(file.getName())) {
          boolean readmeFileFound = false;
          for (File testsSiblings : parentFile.listFiles()) {
            if (testsSiblings.isFile() && ("README.md".equals(testsSiblings.getName()) || "README.markdown".equals(testsSiblings.getName()))) {
              readmeFileFound = true;
              break;
            }
          }
          if (!readmeFileFound) {
            String path = Directory.fromIOFile(parentFile, project).getPath() != null ? Directory.fromIOFile(parentFile, project).getPath() : parentFile.getName();
            addIssue(ReadmeFilePresentCheck.RULE_KEY, "Add a \"README.md\" file to the \"" + path + "\" Puppet module.");
          }
        } else {
          checkReadmeFilePresent(file);
        }
      }
    }
  }

  private void checkTestsDirectoryPresent(File parentFile) {
    for (File file : parentFile.listFiles()) {
      if (file.isDirectory()) {
        if ("tests".equals(file.getName())) {
          for (File testsSiblings : file.getParentFile().listFiles()) {
            if (testsSiblings.isDirectory() && "manifests".equals(testsSiblings.getName())) {
              addIssue(TestsDirectoryPresentCheck.RULE_KEY, "Replace the \"" + Directory.fromIOFile(file, project).getPath() + "\" directory with an \"examples\" directory.");
              break;
            }
          }
        } else {
          checkTestsDirectoryPresent(file);
        }
      }
    }
  }

  private void addIssue(String ruleKey, String message) {
    ActiveRule activeRule = rulesProfile.getActiveRule(CheckList.REPOSITORY_KEY, ruleKey);
    if (activeRule != null) {
      CodeVisitor check = checks.of(activeRule.getRule().ruleKey());
      if (check != null) {
        Issuable issuable = resourcePerspectives.as(Issuable.class, (Component) project);
        if (issuable != null) {
          Issue issue = issuable.newIssueBuilder().ruleKey(RuleKey.of(CheckList.REPOSITORY_KEY, ruleKey)).message(message).build();
          issuable.addIssue(issue);
        }
      }
    }
  }

}
