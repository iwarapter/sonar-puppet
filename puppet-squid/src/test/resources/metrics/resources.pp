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