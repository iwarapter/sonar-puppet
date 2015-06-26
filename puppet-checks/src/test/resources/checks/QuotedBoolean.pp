file { '/tmp/foo':
  purge => 'true',
}

file { '/tmp/bar':
  purge => 'false',
}