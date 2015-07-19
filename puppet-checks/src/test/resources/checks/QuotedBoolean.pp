file { '/tmp/foo':
  purge => 'true',
}

file { '/tmp/bar':
  purge => 'false',
}

file { '/tmp/foo':
  purge => "true",
}

file { '/tmp/bar':
  purge => "false",
}

file { '/tmp/bar':
  purge => "falsez",
}