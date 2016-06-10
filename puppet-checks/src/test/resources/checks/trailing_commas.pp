file { 'foo':
}

file { 'foo':
  ensure => absent,
}

file { 'foo':
  ensure => link,
  target => '/tmp',
}

file { 'foo':
  ensure => absent  # Noncompliant
}

file { 'foo':
  ensure => link,
  target => '/tmp'  # Noncompliant
}

File {
}

File {
  ensure => absent,
}

File {
  ensure => link,
  target => '/tmp',
}

File {
  ensure => absent  # Noncompliant
}

File {
  ensure => link,
  target => '/tmp'  # Noncompliant
}

File {
}

File['/tmp/foo'] {
  ensure => absent,
}

File['/tmp/foo'] {
  ensure => link,
  target => '/tmp',
}

File['/tmp/foo'] {
  ensure => absent  # Noncompliant
}

File['/tmp/foo'] {
  ensure => link,
  target => '/tmp'  # Noncompliant
}

File['/tmp/foo'] {
  ensure => link,
  target +> '/tmp'  # Noncompliant
}

{}

{ key => 'abc', }

{
  key => 'abc',
  key => 'abc',
}

{ key => 'abc' }  # Noncompliant

{
  key => 'abc',
  key => 'abc'   # Noncompliant
}

$rootgroup = $osfamily ? {
  'Solaris' => 'wheel',
}

$rootgroup = $osfamily ? {
  'Solaris' => 'wheel',
  'Debian'  => 'wheel',
}

$rootgroup = $osfamily ? {
  'Solaris' => 'wheel'   # Noncompliant
}

$rootgroup = $osfamily ? {
  'Solaris' => 'wheel',
  'Debian'  => 'wheel'   # Noncompliant
}

class a {}

class a (
) {}

class a (
  $abc = def,
) {}

class a (
  $abc,
) {}

class a (
  $abc,
  $def = 'abc',
) {}

class a (
  $abc = def  # Noncompliant
) {}

class a (
  $abc  # Noncompliant
) {}

class a (
  $abc,
  $def = 'abc'  # Noncompliant
) {}

class a {
  sonarqube::plugin {
    'sonar-css-plugin':
      abc => 'abc',
      version => $css; # Compliant
    'sonar-java-plugin':
      abc => 'abc',
      version => $java; # Compliant
  }
}

class a {
  sonarqube::plugin {
    'sonar-css-plugin':
      abc => 'abc',
      version => $css,; # Compliant
    'sonar-java-plugin':
      abc => 'abc',
      version => $java,; # Compliant
  }
}

class a {
  sonarqube::plugin {
    'sonar-css-plugin':
      abc => 'abc',
      version => $css # Noncompliant
  }
}
