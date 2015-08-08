file { '/tmp/readme.txt':
  owner  => '0',
  group  => '0',
  ensure => file,
  owner  => '0',  # Noncompliant
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
  owner  => '0',  # Noncompliant
  group  => '0',
  ensure => file,
  owner  => '0',  # Noncompliant
  mode   => '0644',
}

File {
  owner  => '0',
  owner  => '0',  # Noncompliant
}

file {
  '/tmp/readme.txt':
    owner  => '0';

  '/tmp/readme2.txt':
    owner  => '0',
    owner  => '0';  # Noncompliant

  '/tmp/readme3.txt':
    owner  => '0';
}

File['log.conf'] {
  owner => '0',
}

File['log.conf'] {
  owner => '0',
  owner => '0',  # Noncompliant
}

File['log.conf'] {
  owner => '0',
  owner +> '0',  # Noncompliant
}

File['log.conf'] {
  owner +> '0',
  owner => '0',  # Noncompliant
}

file { 'foo':
  owner  => '0',
}

file { 'foo':
}

File {
}

File['log.conf'] {
}
