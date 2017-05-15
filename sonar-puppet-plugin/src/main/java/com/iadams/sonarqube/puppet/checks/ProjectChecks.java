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

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.InputComponent;
import org.sonar.api.batch.fs.InputDir;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.rule.RuleKey;

public class ProjectChecks {

  private final SensorContext context;

  private static final Logger LOGGER = LoggerFactory.getLogger(ProjectChecks.class);
  private static final int readmeCheckDepth = 4;
  private static final int metadataCheckDepth = 4;
  private static final int testsCheckDepth = 4;

  public ProjectChecks(SensorContext context) {
    this.context = context;
  }

  public void reportProjectIssues() {
    if (context.fileSystem().baseDir() != null) {
      checkTestsDirectoryPresent(context.fileSystem().baseDir(), 0);
      checkMetadataJsonFilePresent(context.fileSystem().baseDir(), 0);
      checkReadmeFilePresent(context.fileSystem().baseDir(), 0);
    }
  }

  private void checkMetadataJsonFilePresent(File parentFile, int depth) {
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
            InputDir inputDir = context.fileSystem().inputDir(parentFile);
            if (inputDir == null) {
              addIssue(MetadataJsonFilePresentCheck.RULE_KEY, "Add a \"metadata.json\" file to the \"" + context.module().key() + "\" Puppet module.", context.module());
            } else {
              addIssue(MetadataJsonFilePresentCheck.RULE_KEY, "Add a \"metadata.json\" file to the \"" + inputDir.relativePath() + "\" Puppet module.", inputDir);
            }
          }
        } else {
          depth++;
          if (depth < metadataCheckDepth) {
            checkMetadataJsonFilePresent(file, depth);
          }
        }
      }
    }
  }

  private void checkReadmeFilePresent(File parentFile, int depth) {
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
            InputDir inputDir = context.fileSystem().inputDir(parentFile);
            if (inputDir == null) {
              addIssue(ReadmeFilePresentCheck.RULE_KEY, "Add a \"README.md\" file to the \"" + context.module().key() + "\" Puppet module.", context.module());
            } else {
              addIssue(ReadmeFilePresentCheck.RULE_KEY, "Add a \"README.md\" file to the \"" + inputDir.relativePath() + "\" Puppet module.", inputDir);
            }
          }
        } else {
          depth++;
          if (depth < readmeCheckDepth) {
            checkReadmeFilePresent(file, depth);
          }
        }
      }
    }
  }

  private void checkTestsDirectoryPresent(File parentFile, int depth) {
    for (File file : parentFile.listFiles()) {
      if (file.isDirectory()) {
        if ("tests".equals(file.getName())) {
          LOGGER.info("Path: " + file.getPath());
          for (File testsSiblings : file.getParentFile().listFiles()) {
            InputDir inputDir = context.fileSystem().inputDir(file);
            if (testsSiblings.isDirectory()
              && "manifests".equals(testsSiblings.getName())) {
              if (inputDir == null) {
                addIssue(TestsDirectoryPresentCheck.RULE_KEY, "Replace the \"tests\" directory with an \"examples\" directory.", context.module());
                break;
              } else {
                addIssue(TestsDirectoryPresentCheck.RULE_KEY, "Replace the \"" + inputDir.path() + "\" directory with an \"examples\" directory.", inputDir);
                break;
              }
            }
          }
        } else {
          depth++;
          if (depth < testsCheckDepth) {
            checkTestsDirectoryPresent(file, depth);
          }
        }
      }
    }
  }

  protected void addIssue(String ruleKey, String message, InputComponent inputComponent) {
    LOGGER.info("Adding issue: " + ruleKey + " " + message);
    NewIssue newIssue = context
      .newIssue()
      .forRule(RuleKey.of(CheckList.REPOSITORY_KEY, ruleKey));

    newIssue.at(newLocation(inputComponent, newIssue, message)).save();
  }

  private static NewIssueLocation newLocation(InputComponent input, NewIssue issue, String message) {
    return issue.newLocation()
      .on(input).message(message);
  }

}
