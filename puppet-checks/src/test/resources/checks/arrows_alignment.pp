$myhash = {
}

$myhash = {
  'key1' => 'value1',
}

$myhash = {
  'key1'  => 'value1',  # Noncompliant
}

$myhash = {
  'key1' => 'value1',
  'key2' => 'value2',
}

$myhash = {
  'key1' => 'value1',
  'key2'   => 'value2',  # Noncompliant
}

$myhash = {
  'key1'   => 'value1',  # Noncompliant
  'key2' => 'value2',
}

$myhash = {
  'key1'   => 'value1',  # Noncompliant
  'key2'   => 'value2',
}

abc { 'foo':
}

abc { 'foo':
  key1 => 'value1',
}

abc { 'foo':
  key1  => 'value1',  # Noncompliant
}

abc { 'foo':
  key1 => 'value1',
  key2 => 'value2',
}

abc { 'foo':
  key1 => 'value1',
  key2  => 'value2',  # Noncompliant
}

abc { 'foo':
  key1  => 'value1',  # Noncompliant
  key2 => 'value2',
}

abc { 'foo':
  key1  => 'value1',  # Noncompliant
  key2  => 'value2',
}

$rootgroup = $osfamily ? {
  'Solaris' => 'wheel',
}

$rootgroup = $osfamily ? {
  'Solaris'  => 'wheel',  # Noncompliant
}

$rootgroup = $osfamily ? {
  'Solaris'          => 'wheel',
  /(Darwin|FreeBSD)/ => 'wheel',
}

$rootgroup = $osfamily ? {
  'Solaris'          => 'wheel',
  /(Darwin|FreeBSD)/  => 'wheel',  # Noncompliant
}

$rootgroup = $osfamily ? {
  'Solaris'           => 'wheel',  # Noncompliant
  /(Darwin|FreeBSD)/ => 'wheel',
}

$rootgroup = $osfamily ? {
  'Solaris'           => 'wheel',  # Noncompliant
  /(Darwin|FreeBSD)/  => 'wheel',
}

exec { 'cmd':
  path => '/usr/bin', cwd => '/tmp',   # Noncompliant: there should be only one attribute/value pair per line
}

File {
}

File {
  key1  => 'value1',  # Noncompliant
}

File {
  key1  => 'value1',
  key2   => 'value2',  # Noncompliant
}

File <| title == '/root/testFile2' |> {
}

File <| title == '/root/testFile2' |> {
  ensure => absent,
  test   => 'abc',
}

File <| title == '/root/testFile2' |> {
  ensure  => absent,  # Noncompliant
  test   => 'abc',
}

File <| title == '/root/testFile2' |> {
  ensure  => absent,  # Noncompliant
  test    => 'abc',
}

File['foo'] {
  key1 => 'value1',
  key2 +> 'value2',
}

File['foo'] {
  key1 => 'value1',
  key2  +> 'value2',  # Noncompliant
}

File['foo'] {
  key1  => 'value1',  # Noncompliant
  key2 +> 'value2',
}

File['foo'] {
  key1  => 'value1',  # Noncompliant
  key2  +> 'value2',
}
