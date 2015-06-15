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