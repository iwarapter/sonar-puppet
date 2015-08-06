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
