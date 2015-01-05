file { '/tmp/foo':
  owner  => 'root',
  group  => 'root',
  ensure => present,
}