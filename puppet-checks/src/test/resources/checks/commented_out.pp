# = Define apache::htpasswd
# $abc => "ddd",
# fail ('blabla')
define abc {

/* if $with_data_extraction == true and $instance_type == 'author' {
  if $nexus_password == undef {
    fail('Please set \'nexus_password\' in Hiera')
  }
  if $cq_password == undef {
    fail('Please set \'cq_password\' in Hiera')
  }
  notice('Installing components for CQ data extraction')
  class { 'nesclub3::nc3_cq::data_extract':
    nexus_username => $nexus_username,
    nexus_password => $nexus_password,
    maven_version  => $maven_version,
    script_path    => $script_path,
    cq_username    => $cq_username,
    cq_password    => $cq_password,
  }
}
*/

#if $with_data_extraction == true and $instance_type == 'author' {
#  if $nexus_password == undef {
#    fail('Please set \'nexus_password\' in Hiera')
#  }
#  if $cq_password == undef {
#    fail('Please set \'cq_password\' in Hiera')
#  }
#  notice('Installing components for CQ data extraction')
#  class { 'nesclub3::nc3_cq::data_extract':
#    nexus_username => $nexus_username,
#    nexus_password => $nexus_password,
#    maven_version  => $maven_version,
#    script_path    => $script_path,
#    cq_username    => $cq_username,
#    cq_password    => $cq_password,
#  }
#}

}


