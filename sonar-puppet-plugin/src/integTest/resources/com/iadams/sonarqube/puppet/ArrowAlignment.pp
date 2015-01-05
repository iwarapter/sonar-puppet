file { '/tmp/foo':
  ensure => present,
  mode => '0444',
}