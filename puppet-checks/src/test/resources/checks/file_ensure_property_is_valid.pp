file { '/var/log/syslog':
  ensure => link,
  target => '/var/log/messages',
}

file { '/var/log/syslog':
  ensure => '/var/log/messages', # Noncompliant
}

file {
  '/etc/rc.d':
    ensure => directory,
    mode   => '755';

  '/etc/rc.d/init.d':
    ensure => directory,
    mode   => '0755';

  '/etc/rc.d/rc0.d':
    ensure => '/etc/rc.d/rc0.d', # Noncompliant
    mode   => '0755';
}

file { '/etc/example':
  ensure  => directory,
}

file { '/etc/example':
  ensure  => absent,
}

file { '/etc/example':
  ensure  => false,
}

file { '/etc/example':
  ensure  => file,
}

file { '/etc/current':
  target  => '/etc/example',
  ensure  => link,
}

file { '/etc/example':
  ensure  => 'directory',
}

File {
  ensure => '/etc/rc.d/rc0.d', # Noncompliant
}

File {
  ensure => present,
}

File['/etc/current'] {
  ensure => '/etc/example', # Noncompliant
}

File {
  ensure => present,
}

File <<| tag == $tag |>> {
  ensure => '/etc/example', # Noncompliant
}

File <<| tag == $tag |>> {
  ensure => present,
}

