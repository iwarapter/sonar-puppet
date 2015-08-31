#SonarQube Puppet Plugin
 [![Build Status](https://travis-ci.org/iwarapter/sonar-puppet.svg?branch=master)](https://travis-ci.org/iwarapter/sonar-puppet)

##Description

This plugin enables analysis of [Puppet] code. It comes with more than 60 rules spanning from style checks (i.e. All arrows in attribute/value list should be aligned) to detection of potential bugs (i.e. Duplicated parameters should be removed).
This is currently written to support the grammar for [Puppet 3.8], support for the future parser is not currently available.

##Requirements
- SonarQube Server must be up and running. If it's not the case, see [Setup and Upgrade].
- [SonarQube Runner] is installed and can be called from the command line.
- Puppet Plugin is installed on SonarQube Server. See [Installing a Plugin] for more details.

##Getting Started in Less Than Two Minutes
To get started in less than two minutes, you can download the package at https://github.com/racodond/package-test-sonarqube-puppet and follow the instructions in the README file.

##Getting Started
To run an analysis of your Puppet project, we recommend to use [SonarQube Runner].
There are two strategies:
 1. Analyze each of your modules separately
 2. Analyze all your modules at once
 
###Analyze each of your modules separately
It means that one SonarQube project is created for each Puppet module.
Let's take the Puppet Labs Apache module at https://github.com/puppetlabs/puppetlabs-apache as an example.

- Clone this project (let's say in the `/home/user/puppet` directory)
- Add a `sonar-project.properties` file to the `/home/user/puppet` directory with the following content:
```
sonar.projectKey=puppetlabs-apache
sonar.projectName=Puppet Labs Apache Module
sonar.sources=puppetlabs-apache
sonar.projectVersion=1.4.1
```
- Run `sonar-runner` from `/home/user/puppet`

###Analyze all your modules at once
It means that one single SonarQube project is created for all the Puppet modules.

- Retrieve the source code of your modules with your favorite tool (such as r10k), let's say in `/home/user/puppet`. The tree structure should be the following:
```
/home/user/puppet
   |--modules
      |--mymodule1
         |--manifests
            |--init.pp
            |--...
      |--mymodule2
         |--manifests
            |--init.pp
            |--...
```
- Add a `sonar-project.properties` file to the `/home/user/puppet` directory with the following content:
```
sonar.projectKey=my-puppet-code-base
sonar.projectName=My Puppet Code Base
sonar.sources=modules
sonar.projectVersion=1.0
```    
- Run `sonar-runner` from `/home/user/puppet`


###Which strategy to choose?
The first strategy easily allows you to automate a SonarQube analysis of a module each time a developer pushes some code to the VCS. By providing a quick feedback to developers, you can ensure that quality flaws are quickly fixed.

The second strategy provides a quality overview of your entire Puppet Code Base and how it evolves overtime. Moreover, analyzing your whole code base at once allows you to benefit from more advanced cross-module checks (such as the [autoloader layout rule]).

We recommend you to apply both strategies to get all the benefits.

##Metrics

The Puppet terms do not always match with the standard [SonarQube metrics]. Here's the list of slight differences:
- Classes = Number of classes + Number of defines
- Functions = Number of resources (including default resource and resource override)
- Complexity is increased by one for each: class, define, resource instance, resource default statement, resource override, if, elsif, unless, selector match, case match, and, or.


##Extending Coding Rules using XPath

New coding rules can be added using XPath. See the related [documentation].
To navigate the AST, download the [SSLR Puppet Toolkit].

[Puppet]:https://puppetlabs.com/
[Setup and Upgrade]:http://docs.sonarqube.org/display/SONAR/Setup+and+Upgrade
[SonarQube Runner]:http://docs.sonarqube.org/display/SONAR/Installing+and+Configuring+SonarQube+Runner
[Installing a Plugin]:http://docs.sonarqube.org/display/SONAR/Installing+a+Plugin
[documentation]:http://docs.sonarqube.org/display/SONAR/Extending+Coding+Rules
[SSLR Puppet Toolkit]:https://github.com/iwarapter/sonar-puppet/releases/latest
[Puppet 3.8]:https://docs.puppetlabs.com/puppet/3.8/reference/index.html
[SonarQube metrics]:http://docs.sonarqube.org/display/SONAR/Metric+
[autoloader layout rule]:https://github.com/iwarapter/sonar-puppet/blob/master/puppet-checks/src/main/resources/org/sonar/l10n/pp/rules/puppet/AutoLoaderLayout.html