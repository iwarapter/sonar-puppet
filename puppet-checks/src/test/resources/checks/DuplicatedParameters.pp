file { '/tmp/readme.txt':
  owner  => '0',
  group  => '0',
  ensure => file,
  owner  => '0',
  mode   => '0644',
}

file { '/tmp/readme.txt':
  owner  => '0',
  group  => '0',
  ensure => file,
  mode   => '0644',
}

file { '/tmp/readme.txt':
  owner  => '0',
  owner  => '0',
  group  => '0',
  ensure => file,
  owner  => '0',
  mode   => '0644',
}

File {
  owner  => '0',
  owner  => '0',
}

file {
  '/tmp/readme.txt':
    owner  => '0';

  '/tmp/readme2.txt':
    owner  => '0',
    owner  => '0';

  '/tmp/readme3.txt':
    owner  => '0';
}

File['log.conf'] {
  owner => '0',
}

File['log.conf'] {
  owner => '0',
  owner => '0',
}

file { 'foo':
  owner  => '0',
}
