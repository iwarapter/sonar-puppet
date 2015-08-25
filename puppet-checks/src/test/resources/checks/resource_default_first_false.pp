class abc {

  File {
    owner => 'user1',
  }

  file { '/tmp/foo.txt':
    ensure => present,
  }

  Group {               # Noncompliant
    name => 'group1',
  }

}