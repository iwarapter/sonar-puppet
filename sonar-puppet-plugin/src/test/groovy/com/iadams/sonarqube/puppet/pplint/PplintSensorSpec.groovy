package com.iadams.sonarqube.puppet.pplint

import com.google.common.collect.ImmutableList
import com.iadams.sonarqube.puppet.PuppetPlugin
import com.iadams.sonarqube.puppet.core.Puppet
import org.apache.maven.project.MavenProject
import org.sonar.api.component.ResourcePerspectives
import org.sonar.api.profiles.RulesProfile
import org.sonar.api.resources.Project
import org.sonar.api.rules.ActiveRule
import org.sonar.api.rules.RuleFinder
import org.sonar.api.scan.filesystem.FileQuery
import org.sonar.api.scan.filesystem.ModuleFileSystem
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by iwarapter
 */
class PplintSensorSpec extends Specification {

    private ModuleFileSystem fs
    private RuleFinder ruleFinder
    private PplintConfiguration conf
    private RulesProfile profile

    def setup(){
        ruleFinder = Mock(RuleFinder)
        conf = Mock(PplintConfiguration)
        profile = Mock(RulesProfile)
        fs = Mock(ModuleFileSystem)
    }

    def "shouldnt thrown when instantiating"() {
        expect:
        new PplintSensor(ruleFinder, conf, profile, fs, Mock(ResourcePerspectives))
    }

    @Unroll
    def "should execute on "() {
        // which means: only on puppet projects and only if
        // there is at least one active pplint rule

        when:
        fs.files(_) >> files
        PplintSensor sensor = new PplintSensor(ruleFinder, conf, langProfile, fs, Mock(ResourcePerspectives))

        then:
        sensor.shouldExecuteOnProject(project) == outcome

        where:
        files               | project                               | langProfile           | outcome
        [new File("/tmp")]  | createProjectForLanguage(Puppet.KEY)  | createPplintProfile() | true
        [new File("/tmp")]  | createProjectForLanguage(Puppet.KEY)  | createEmptyProfile()  | false
        []                  | createProjectForLanguage('whatever')  | createPplintProfile() | false
        []                  | createProjectForLanguage('whatever')  | createEmptyProfile()  | false
    }

    private Project createProjectForLanguage(String languageKey){
        Project project = Mock(Project)
        project.getLanguageKey() >> languageKey
        return project
    }

    private RulesProfile createEmptyProfile() {
        RulesProfile profile = Mock(RulesProfile)
        profile.getActiveRulesByRepository(PplintRuleRepository.REPOSITORY_KEY) >> []

        return profile
    }

    private RulesProfile createPplintProfile() {
        def rules = [Mock(ActiveRule)]

        RulesProfile profile = Mock(RulesProfile)
        profile.getActiveRulesByRepository(PplintRuleRepository.REPOSITORY_KEY) >> rules

        return profile
    }
}
