package com.iadams.sonarqube.puppet.pplint

import groovy.util.logging.Slf4j
import org.sonar.api.batch.Sensor
import org.sonar.api.batch.SensorContext
import org.sonar.api.component.ResourcePerspectives
import org.sonar.api.issue.Issuable
import org.sonar.api.profiles.RulesProfile
import org.sonar.api.resources.Project
import org.sonar.api.rule.RuleKey
import org.sonar.api.rules.Rule
import org.sonar.api.rules.RuleFinder
import org.sonar.api.scan.filesystem.FileQuery
import org.sonar.api.scan.filesystem.ModuleFileSystem
import org.sonar.api.utils.SonarException
import com.iadams.sonarqube.puppet.core.Puppet

/**
 * Created by iwarapter
 */
@Slf4j
class PplintSensor implements Sensor{

    private RuleFinder ruleFinder
    private RulesProfile profile
    private PplintConfiguration conf
    private ModuleFileSystem fileSystem
    private ResourcePerspectives resourcePerspectives


    PplintSensor(RuleFinder ruleFinder, PplintConfiguration conf, RulesProfile profile, ModuleFileSystem fileSystem, ResourcePerspectives resourcePerspectives) {
        this.ruleFinder = ruleFinder;
        this.conf = conf;
        this.profile = profile;
        this.fileSystem = fileSystem;
        this.resourcePerspectives = resourcePerspectives;
    }

    boolean shouldExecuteOnProject(Project project) {
        return (!fileSystem.files(FileQuery.onSource().onLanguage(Puppet.KEY)).isEmpty()
        && !profile.getActiveRulesByRepository(PplintRuleRepository.REPOSITORY_KEY).isEmpty())
    }

    void analyse(Project project, SensorContext sensorContext) {
        File workdir = new File(fileSystem.workingDir(), "/pplint/")
        prepareWorkDir(workdir)
        int i = 0
        for (File file : fileSystem.files(FileQuery.onSource().onLanguage(Puppet.KEY))) {
            try {
                File out = new File(workdir, i + ".out")
                analyzeFile(file, out, project)
                i++
            } catch (Exception e) {
                String msg = "Cannot analyse the file '${file.getAbsolutePath()}', details: '${e}'"
                throw new SonarException(msg, e)
            }
        }
    }

    protected void analyzeFile(File file, File out, Project project) throws IOException {
        org.sonar.api.resources.File ppfile = org.sonar.api.resources.File.fromIOFile(file, project)

        String pplintPath = conf.getPplintPath()

        PplintIssuesAnalyzer analyzer = new PplintIssuesAnalyzer(pplintPath)
        List<Issue> issues = analyzer.analyze(file.getAbsolutePath(), out)

        for (Issue pplintIssue : issues) {
            Rule rule = ruleFinder.findByKey(PplintRuleRepository.REPOSITORY_KEY, pplintIssue.ruleId)

            if (rule != null) {
                if (rule.isEnabled()) {
                    Issuable issuable = resourcePerspectives.as(Issuable, ppfile)

                    if (issuable != null) {
                        org.sonar.api.issue.Issue issue = issuable.newIssueBuilder()
                                .ruleKey(RuleKey.of(rule.getRepositoryKey(), rule.getKey()))
                                .line(pplintIssue.line)
                                .message(pplintIssue.descr)
                                .build();
                        issuable.addIssue(issue)
                    }
                } else {
                    log.info "Pplint rule ${pplintIssue.ruleId} is disabled in Sonar"
                }
            } else {
                log.warn "Pplint rule ${pplintIssue.ruleId} is unknown in Sonar"
            }
        }
    }

    private static void prepareWorkDir(File dir) {
        try {
            dir.mkdirs()
            // directory is cleaned, because Sonar 3.0 will not do this for us
            dir.eachDirRecurse { subDir->
                subDir.eachFile{ it.delete() }
            }
        } catch (IOException e) {
            throw new SonarException("Cannot create directory: ${dir}", e)
        }
    }
}
