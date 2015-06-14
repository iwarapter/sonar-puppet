/**
 * Sonar Puppet Plugin
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

import com.google.common.base.Charsets;
import com.iadams.sonarqube.puppet.Puppet;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionXmlLoader;
import org.sonar.squidbridge.rules.SqaleXmlLoader;

import java.util.List;

/**
 * @author iwarapter
 */
public class PplintRuleRepository implements RulesDefinition {

    public static final String REPOSITORY_NAME = "Pplint";
    public static final String REPOSITORY_KEY = REPOSITORY_NAME;

    private static final String RULES_FILE = "/com/iadams/sonarqube/puppet/pplint/rules.xml";
    private static final String SQALE_FILE = "/com/sonar/sqale/puppet-model.xml";
    private final RulesDefinitionXmlLoader xmlLoader;

    public PplintRuleRepository(RulesDefinitionXmlLoader xmlLoader) {
        this.xmlLoader = xmlLoader;
    }

    @Override
    public void define(Context context){
        NewRepository repository = context
                .createRepository(REPOSITORY_KEY, Puppet.KEY)
                .setName(REPOSITORY_NAME);
        xmlLoader.load(repository, getClass().getResourceAsStream(RULES_FILE), Charsets.UTF_8.name());
        SqaleXmlLoader.load(repository, SQALE_FILE);
        repository.done();
    }
}
