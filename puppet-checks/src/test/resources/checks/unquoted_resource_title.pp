file { foo:    # Noncompliant
}

file { $foo:
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
  $var,
]:
  ensure => directory,
}