package com.iadams.sonarqube.puppet.samples

import spock.lang.Requires
import spock.lang.Unroll

import static com.iadams.sonarqube.puppet.samples.AbstractSampleSpec.isPuppetAvailable

class UnquotedResourceTitleSampleSpec extends AbstractSampleSpec {

  @Unroll
  @Requires({ isPuppetAvailable() })
  def "the puppet code is valid in sample"() {
    expect:
    validateSample(sample)

    where:
    sample << findSamples('src/main/resources/org/sonar/l10n/pp/rules/puppet/UnquotedResourceTitle.html')
  }
}
