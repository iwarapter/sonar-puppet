file { 'foo':
  mode => '777'
}

file { 'foo':
  mode => '0777'
}

file { 'foo':
  mode => $file_mode
}

file { 'foo':
  mode => 'u=rw,og=r'
}

file { 'foo':
  mode => undef
}

file { 'foo':
  mode => 'undef'
}

file { '/etc/passwd':
  audit => [ owner, mode ],
}

file {
	'/etc/rc.d':
		ensure => directory,
		mode   => '755';

	'/etc/rc.d/init.d':
		ensure => directory,
		mode   => '0755';

	'/etc/rc.d/rc0.d':
		ensure => directory,
		mode   => '0755';
}