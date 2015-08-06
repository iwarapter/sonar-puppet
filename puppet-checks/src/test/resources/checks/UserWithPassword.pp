user { 'iain':
	password => 'cheese'
}

group { 'iain':
	password => 'cheese'
}

user { 'iain':
	password2 => 'cheese'
}

user {
	'iain':
	  password => 'cheese';
  'david':
		password => 'cheese';
	'toto':
		password2 => 'cheese';
}
