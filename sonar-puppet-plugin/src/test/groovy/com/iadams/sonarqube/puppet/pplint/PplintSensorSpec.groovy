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
package com.iadams.sonarqube.puppet.pplint

import com.iadams.sonarqube.puppet.Puppet
import org.sonar.api.component.ResourcePerspectives
import org.sonar.api.profiles.RulesProfile
import org.sonar.api.resources.Project
import org.sonar.api.rules.ActiveRule
import org.sonar.api.rules.RuleFinder
import org.sonar.api.scan.filesystem.ModuleFileSystem
import spock.lang.Specification
import spock.lang.Unroll

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
