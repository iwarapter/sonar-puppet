file { foo:    # Noncompliant
}

file { $foo:   # Noncompliant
}

file { [
  abc,       # Noncompliant
  $var,      # Noncompliant
]:
  ensure => directory,
}

file { 'foo':
}

file { "${foo}":
}

file { [
  'abc',
  "${var}",
]:
  ensure => directory,
}