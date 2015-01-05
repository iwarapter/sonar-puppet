package com.iadams.sonarqube.puppet

import com.iadams.sonarqube.puppet.core.Puppet
import org.sonar.commonrules.api.CommonRulesEngine;
import org.sonar.commonrules.api.CommonRulesRepository;

/**
 * Created by iwarapter
 */
class PuppetCommonRulesEngine extends CommonRulesEngine {

    public PuppetCommonRulesEngine() {
        super(Puppet.KEY);
    }

    @Override
    protected void doEnableRules(CommonRulesRepository repository) {
        repository
                .enableDuplicatedBlocksRule()
                .enableInsufficientCommentDensityRule(null)
                .enableInsufficientLineCoverageRule(null)
                .enableInsufficientBranchCoverageRule(null)
    }
}