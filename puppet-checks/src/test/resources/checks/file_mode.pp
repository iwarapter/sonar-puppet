file { 'foo':
  mode => '777'  # Noncompliant
}

file { 'foo':
  mode => '0777'
}

file { 'foo':
  mode => $file_mode
}

file { 'foo':
  mode => 'u=rw,og=r'
}

file { 'foo':
  mode => undef
}

file { 'foo':
  mode => 'undef'  # Noncompliant
}

file { '/etc/passwd':
  audit => [ owner, mode ],
}

file {
  '/etc/rc.d':
    ensure => directory,
    mode   => '755';  # Noncompliant

  '/etc/rc.d/init.d':
    ensure => directory,
    mode   => '0755';

  '/etc/rc.d/rc0.d':
    ensure => directory,
    mode   => '0755';
}

File {
  mode => '755',  # Noncompliant
}

File {
  mode => '0755',
}

file { 'foo':
  mode => 755,  # Noncompliant
}

file { 'foo':
  mode => 0755,  # Noncompliant
}

File {
  mode => 755,  # Noncompliant
}

File {
  mode => 0755,  # Noncompliant
}

file { 'foo':
  mode => "0755",  # Noncompliant
}

file { 'foo':
  mode => "abc",  # Noncompliant
}

File {
  mode => "0755",  # Noncompliant
}

File {
  mode => "abc",  # Noncompliant
}

file { 'foo':
  mode => "u=${var}",
}

File {
  mode => "u=${var}",
}

File['/tmp/foo'] {
  mode => '755',  # Noncompliant
}

File['/tmp/foo'] {
  mode => '0755',
}

File['/tmp/foo'] {
  mode => 755,  # Noncompliant
}

File['/tmp/foo'] {
  mode => 0755,  # Noncompliant
}

File['/tmp/foo'] {
  mode => "0755",  # Noncompliant
}

File['/tmp/foo'] {
  mode => "abc",  # Noncompliant
}

File['/tmp/foo'] {
  mode => "u=${var}",
}

File <| title == 'luke' |> {
  mode => '755',  # Noncompliant
}

File <| title == 'luke' |> {
  mode => '0755',
}

File <| title == 'luke' |> {
  mode => 755,  # Noncompliant
}

File <| title == 'luke' |> {
  mode => 0755,  # Noncompliant
}

File <| title == 'luke' |> {
  mode => "0755",  # Noncompliant
}

File <| title == 'luke' |> {
  mode => "abc",  # Noncompliant
}

File <| title == 'luke' |> {
  mode => "u=${var}",
}
