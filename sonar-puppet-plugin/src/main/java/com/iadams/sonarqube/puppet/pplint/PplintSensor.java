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
package com.iadams.sonarqube.puppet.pplint;

import com.iadams.sonarqube.puppet.Puppet;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.issue.Issuable;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.scan.filesystem.FileQuery;
import org.sonar.api.scan.filesystem.ModuleFileSystem;
import org.sonar.api.utils.SonarException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PplintSensor implements Sensor {

    private static final Logger LOG = LoggerFactory.getLogger(PplintSensor.class);

    private RuleFinder ruleFinder;
    private RulesProfile profile;
    private PplintConfiguration conf;
    private ModuleFileSystem fileSystem;
    private ResourcePerspectives resourcePerspectives;

    public PplintSensor(RuleFinder ruleFinder, PplintConfiguration conf, RulesProfile profile, ModuleFileSystem fileSystem, ResourcePerspectives resourcePerspectives) {
        this.ruleFinder = ruleFinder;
        this.conf = conf;
        this.profile = profile;
        this.fileSystem = fileSystem;
        this.resourcePerspectives = resourcePerspectives;
    }

    public boolean shouldExecuteOnProject(Project project) {
        return !fileSystem.files(FileQuery.onSource().onLanguage(Puppet.KEY)).isEmpty()
            && !profile.getActiveRulesByRepository(PplintRuleRepository.REPOSITORY_KEY).isEmpty();
    }

    public void analyse(Project project, SensorContext sensorContext) {
        File workdir = new File(fileSystem.workingDir(), "/pplint/");
        prepareWorkDir(workdir);
        int i = 0;
        for (File file : fileSystem.files(FileQuery.onSource().onLanguage(Puppet.KEY))) {
            try {
                File out = new File(workdir, (i + ".out"));
                analyzeFile(file, out, project);
                i++;
            } catch (Exception e) {
                String msg = new StringBuilder()
                        .append("Cannot analyse the file '")
                        .append(file.getAbsolutePath())
                        .append("', details: '")
                        .append(e)
                        .append("'")
                        .toString();
                throw new SonarException(msg, e);
            }

        }

    }

    protected void analyzeFile(File file, File out, Project project) throws IOException {
        org.sonar.api.resources.File ppfile = org.sonar.api.resources.File.fromIOFile(file, project);

        String pplintPath = conf.getPplintPath();

        PplintIssuesAnalyzer analyzer = new PplintIssuesAnalyzer(pplintPath);
        List<Issue> issues = analyzer.analyze(file.getAbsolutePath(), fileSystem.sourceCharset(), out);

        for (Issue pplintIssue : issues) {
            Rule rule = ruleFinder.findByKey(PplintRuleRepository.REPOSITORY_KEY, pplintIssue.getRuleId());

            if (rule != null) {
                if (rule.isEnabled()) {
                    Issuable issuable = resourcePerspectives.as(Issuable.class, ppfile);

                    if (issuable != null) {
                        org.sonar.api.issue.Issue issue = issuable.newIssueBuilder()
                                .ruleKey(RuleKey.of(rule.getRepositoryKey(), rule.getKey()))
                                .line(pplintIssue.getLine())
                                .message(pplintIssue.getDescr())
                                .build();
                        issuable.addIssue(issue);
                    }

                } else {
                    LOG.info("Pplint rule " + pplintIssue.getRuleId() + " is disabled in Sonar");
                }

            } else {
                LOG.warn("Pplint rule " + pplintIssue.getRuleId() + " is unknown in Sonar");
            }

        }

    }

    private static void prepareWorkDir(File dir) {
        try {
            FileUtils.forceMkdir(dir);
            // directory is cleaned, because Sonar 3.0 will not do this for us
            FileUtils.cleanDirectory(dir);
        } catch (IOException e) {
            throw new SonarException("Cannot create directory: " + dir, e);
        }
    }

}
