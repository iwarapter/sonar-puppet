file { '/etc/passwd':
	ensure => file,
	owner  => 'root',
	group  => 'root',
	mode   => '0600',
}

user { $user:
	ensure  => present,
	gid     => $group,
	require => Package['httpd'],
}

User {
	ensure  => present,
	gid     => $group,
	require => Package['httpd'],
}

User['abc'] {
	ensure  => present,
	gid     => $group,
	require => Package['httpd'],
}

file {
'/etc/passwd':
	ensure => file,
	owner  => 'root',
	group  => 'root',
	mode   => '0600',
	;
'/etc/passwd2':
  ensure => file,
  owner  => 'root',
  group  => 'root',
  mode   => '0600',
	;
}

if $a {}

if $a {}
elsif $b {}
else {}

if $a or $a and $a {}

unless $a {}

case $a {
	'aa': {}
	/d+/: {}
  default: {}
}

$a = $b ? {
  'aa'    => 'abc',
  /d/     => 'abc',
  default => 'abc',
}

class abc {}
define abc {}
