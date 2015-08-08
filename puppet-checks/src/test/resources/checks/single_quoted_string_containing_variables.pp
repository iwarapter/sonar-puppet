fail 'Hello ${guy}!' # Noncompliant
fail 'The dollar sign: $, ...'

class {'apache':
  version => '${version}', # Noncompliant
}

class {'apache':
  version => 'abc $ def',
}
