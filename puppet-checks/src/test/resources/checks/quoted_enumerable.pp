package { 'foo':
  ensure   => "present",  # Noncompliant
  ensure   => 'present',  # Noncompliant
  loglevel => "notice",   # Noncompliant
  loglevel => 'notice',   # Noncompliant
}

package { 'foo':
  ensure   => present,
  ensure   => $a,
  ensure   => "${a}sent",
  ensure   => 'blabla',
  ensure   => "blabla",
  loglevel => notice,
}

Package {
  ensure   => "present",  # Noncompliant
  ensure   => 'present',  # Noncompliant
  loglevel => "notice",   # Noncompliant
  loglevel => 'notice',   # Noncompliant
}

Package {
  ensure   => present,
  ensure   => $a,
  ensure   => "${a}sent",
  ensure   => 'blabla',
  ensure   => "blabla",
  loglevel => notice,
}

Package['foo'] {
  ensure   => "present",  # Noncompliant
  ensure   => 'present',  # Noncompliant
  loglevel => "notice",   # Noncompliant
  loglevel => 'notice',   # Noncompliant
}

Package['foo'] {
  ensure   => present,
  ensure   => $a,
  ensure   => "${a}sent",
  ensure   => 'blabla',
  ensure   => "blabla",
  loglevel => notice,
}

Package <<| tag == $tag |>> {
  ensure   => "present",  # Noncompliant
  ensure   => 'present',  # Noncompliant
  loglevel => "notice",   # Noncompliant
  loglevel => 'notice',   # Noncompliant
}

Package <<| tag == $tag |>> {
  ensure   => present,
  ensure   => $a,
  ensure   => "${a}sent",
  ensure   => 'blabla',
  ensure   => "blabla",
  loglevel => notice,
}
