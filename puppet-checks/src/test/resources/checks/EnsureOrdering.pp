file { '/tmp/readme.txt':
  owner  => '0',
  group  => '0',
  ensure => file,
  mode   => '0644',
}

file { '/tmp/readme.txt':
  ensure => file,
  mode   => '0644',
  owner  => '0',
  group  => '0',
}

file { '/tmp/readme.txt':
  mode   => '0644',
  owner  => '0',
  group  => '0',
}

file {
  '/tmp/readme.txt':
    ensure => file,
    mode   => '0644',
    owner  => '0',
    group  => '0',
  ;
  '/tmp/readme2.txt':
    mode   => '0644',
    ensure => file,
    owner  => '0',
    group  => '0',
  ;
}