file { foo:    # Noncompliant
}

file { [
  abc,       # Noncompliant
  $var,
]:
  ensure => directory,
}

file { 'foo':
}

file { [
  'abc',
  "${var}",
]:
  ensure => directory,
}

file { $bar:
}


