SonarQube Puppet Plugin
=======================
 [![Build Status](https://travis-ci.org/iwarapter/sonar-puppet.svg?branch=master)](https://travis-ci.org/iwarapter/sonar-puppet)
 [ ![Download](https://api.bintray.com/packages/iwarapter/sonar-plugins/sonar-puppet/images/download.svg) ](https://bintray.com/iwarapter/sonar-plugins/sonar-puppet/_latestVersion)
 
 [![Stories in Ready](https://badge.waffle.io/iwarapter/sonar-puppet.svg?label=ready&title=Ready)](http://waffle.io/iwarapter/sonar-puppet)
 [![Stories in Ready](https://badge.waffle.io/iwarapter/sonar-puppet.svg?label=In Progress&title=In Progress)](http://waffle.io/iwarapter/sonar-puppet)

Description
-----------
This plugin enables analysis of [Puppet] projects. It comes with rules provided by [Puppet lint].

Requirements
------------
- SonarQube Server must be up and running. If it's not the case, see [Setup and Upgrade].
- [SonarQube Runner] is installed and can be called from the command line.
- Puppet Plugin is installed on SonarQube Server. See [Installing a Plugin] for more details.
- (Optional) [Puppet lint] has to be installed, if you want to activate Puppet lint rules.

Getting Started
---------------
To run an analysis of your Puppet project, we recommend to use SonarQube Runner.

Create your own sonar-project.properties file at the root of your project and then run the command "sonar-runner".

Here is an example for the puppetlabs-apache module:
```
sonar.projectKey=puppetlabs-apache
sonar.projectName=puppetlabs-apache
sonar.sources=manifests
sonar.projectVersion=1.4.1
```

Advanced Configuration
----------------------

Property     | Scope       | Example | Description
------------ | ----------- | ------- | -----------
sonar.puppet.pplint | System-wide | /usr/local/bin/puppet-lint | Path to the puppet-lint executable to use in puppet lint analysis. Set to empty to use the default one (default is puppet-lint).

Extending Coding Rules using XPath
----------------------------------

New coding rules can be added using XPath. See the related [documentation].
To navigate the AST, download the [SSLR Puppet Toolkit].

[Puppet]:https://puppetlabs.com/
[Puppet lint]:http://puppet-lint.com/
[Setup and Upgrade]:http://docs.sonarqube.org/display/SONAR/Setup+and+Upgrade
[SonarQube Runner]:http://docs.sonarqube.org/display/SONAR/Installing+and+Configuring+SonarQube+Runner
[Installing a Plugin]:http://docs.sonarqube.org/display/SONAR/Installing+a+Plugin
[documentation]:http://docs.sonarqube.org/display/SONAR/Extending+Coding+Rules
[SSLR Puppet Toolkit]:https://bintray.com/iwarapter/sonar-plugins/sonar-puppet/_latestVersion#files