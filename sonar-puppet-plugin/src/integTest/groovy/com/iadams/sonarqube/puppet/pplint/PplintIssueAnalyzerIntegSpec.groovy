package com.iadams.sonarqube.puppet.pplint

import com.google.common.base.Charsets
import org.sonar.api.utils.SonarException
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by iwarapter
 */
class PplintIssueAnalyzerIntegSpec extends Specification {

    def "Calling analyse with no puppet lint generates exception"(){
        given:
        String codeChunksResource = "/com/iadams/sonarqube/puppet/HardTabs.pp"
        String codeChunksPathName = getClass().getResource(codeChunksResource).getPath()
        new File('build/test/spec/output').mkdirs()
        File out = new File('build/test/spec/output/error.out')
        out.createNewFile()

        when:
        new PplintIssuesAnalyzer('someFile').analyze(codeChunksPathName, Charsets.UTF_8, out)

        then:
        thrown(SonarException)
    }

    def "check empty file returns no issues"(){
        given:
        String codeChunksResource = "/com/iadams/sonarqube/puppet/Blank.pp"
        String codeChunksPathName = getClass().getResource(codeChunksResource).getPath()
        new File('build/test/spec/output').mkdirs()
        File out = new File('build/test/spec/output/blank.out')
        out.createNewFile()

        when:
        def issues = new PplintIssuesAnalyzer(null).analyze(codeChunksPathName, Charsets.UTF_8, out)

        then:
        issues.isEmpty()
        !out.text.length()
    }

    @Unroll
    def "Check file: #input contains rule: #rule"() {
        when:
        String codeChunksResource = "/com/iadams/sonarqube/puppet/${input}.pp"
        String codeChunksPathName = getClass().getResource(codeChunksResource).getPath()
        new File('build/test/spec/output').mkdirs()
        File out = new File("build/test/spec/output/${input}.out")
        out.createNewFile()

        then:
        def issues = new PplintIssuesAnalyzer(null).analyze(codeChunksPathName, Charsets.UTF_8, out)
        issues.find{ it.ruleId == rule }

        where:
        input                               | rule
        '2SpaceSoftTabs'                    | '2sp_soft_tabs'
        '80CharacterLineLimit'              | '80chars'
        'ArrowAlignment'                    | 'arrow_alignment'
        'AttributeOrdering'                 | 'ensure_first_param'
        'AutoloaderLayout'                  | 'autoloader_layout'
        'CaseWithoutDefault'                | 'case_without_default'
        'ClassInheritsFromParamsClass'      | 'class_inherits_from_params_class'
        //class_parameter_defaults deprecated only in versions below 1.0
        //'class_parameter_defaults'          | 'class_parameter_defaults'
        'Documentation'                     | 'documentation'
        'DoubleQuotedStrings'               | 'double_quoted_strings'
        'DuplicateParameters'               | 'duplicate_params'
        'EnsureNotSymlinkTarget'            | 'ensure_not_symlink_target'
        'FileMode'                          | 'file_mode'
        'HardTabs'                          | 'hard_tabs'
        'InheritsAcrossNamespaces'          | 'inherits_across_namespaces'
        'NamesContainingDash'               | 'names_containing_dash'
        'NestedClassesOrDefines'            | 'nested_classes_or_defines'
        'OnlyVariableString'                | 'only_variable_string'
        'ParameterOrder'                    | 'parameter_order'
        //PuppetUrl will only work with version 1.+ of puppet-lint
        //'PuppetUrlWithoutModules'           | 'puppet_url_without_modules'
        'QuotedBooleans'                    | 'quoted_booleans'
        'RightToLeftRelationship'           | 'right_to_left_relationship'
        'SelectorInsideResource'            | 'selector_inside_resource'
        'SingleQuotedStringWithVariables'   | 'single_quote_string_with_variables'
        'SlashAsteriskComment'              | 'star_comments'
        'SlashComment'                      | 'slash_comments'
        'TrailingWhitespace'                | 'trailing_whitespace'
        'UnquotedFileMode'                  | 'unquoted_file_mode'
        //UnquotedNodeName will only work with version 1.+ of puppet-lint
        //'UnquotedNodeName'                  | 'unquoted_node_name'
        'UnquotedResourceTitle'             | 'unquoted_resource_title'
        'VariableContainsDash'              | 'variable_contains_dash'
        'VariableScope'                     | 'variable_scope'
        'VariablesNotEnclosed'              | 'variables_not_enclosed'
    }
}
