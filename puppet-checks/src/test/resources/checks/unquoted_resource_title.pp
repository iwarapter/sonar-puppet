file { foo:    # Noncompliant
}

file { [
  abc,       # Noncompliant
  $var,      # Noncompliant
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
