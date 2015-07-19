file { '/tmp/readme.txt':
  owner  => '0',
  group  => '0',
  ensure => file,
  mode   => '0644',
}