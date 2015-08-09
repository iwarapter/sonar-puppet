# Copyright 2011 MaestroDev
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
class sonarqube (                                                                        # +1
  $version          = '4.1.2',
  $user             = 'sonar',
  $group            = 'sonar',
  $user_system      = true,
  $service          = 'sonar',
  $installroot      = '/usr/local',
  $home             = undef,
  $host             = undef,
  $port             = 9000,
  $portAjp          = -1,
  $download_url     = 'http://dist.sonar.codehaus.org',
  $context_path     = '/',
  $arch             = $sonarqube::params::arch,
  $ldap             = {},
  $crowd            = {},
  $jdbc             = {
    url      => 'jdbc:h2:tcp://localhost:9092/sonar',
    username => 'sonar',
    password => 'sonar',
  },
  $log_folder       = '/var/local/sonar/logs',
  $updatecenter     = true,
  $http_proxy       = {},
  $profile          = false,
  $web_java_opts    = undef,
  $search_java_opts = undef,
) inherits sonarqube::params {
  Exec {                                                                                 # +1
    path => '/usr/bin:/usr/sbin:/bin:/sbin:/usr/local/bin'
  }
  File {                                                                                 # +1
    owner => $user,
    group => $group
  }

# wget from https://github.com/maestrodev/puppet-wget
  include wget                                                                           # +1

  if versioncmp($version, '4.0') < 0 {                                                   # +1
    $package_name = 'sonar'                                                              # +1
  }
  else {
    $package_name = 'sonarqube'                                                          # +1
  }

  $extensions_dir = "${real_home}/extensions"                                            # +1

  if ! defined(Package[unzip]) {                                                         # +1
    package { 'unzip':                                                                   # +1
      ensure => present,
      before => Exec[untar]
    }
  }

# For convenience, provide "built-in" support for the Sonar LDAP plugin.
  sonarqube::plugin { 'sonar-ldap-plugin' :                                              # +1
    ensure     => empty($ldap) ? {
      true  => absent,
      false => present
    },
    artifactid => 'sonar-ldap-plugin',
    version    => '1.4',
  }

  service { 'sonarqube':                                                                 # +1
    ensure     => running,
    name       => $service,
    hasrestart => true,
    hasstatus  => true,
    enable     => true,
    require    => File["/etc/init.d/${service}"],
  }
}
