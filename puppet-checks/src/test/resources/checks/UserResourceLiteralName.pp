user { 'tim':
	ensure => present
}

user { $username:
	ensure => present
}