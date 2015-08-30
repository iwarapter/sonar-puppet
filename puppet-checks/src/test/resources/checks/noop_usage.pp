package { 'foo':
  ensure => present,
  noop   => true,     # Noncompliant
}

Package {
  ensure => present,
  noop   => true,     # Noncompliant
}

Package['foo'] {
  ensure => present,
  noop   => true,     # Noncompliant
}

Package <| title == 'luke' |> {
  ensure => present,
  noop   => true,     # Noncompliant
}

package { 'foo':
  ensure => present,
}

Package {
  ensure => present,
}

Package['foo'] {
  ensure => present,
}

package { 'foo':
}

Package {
}

Package['foo'] {
}

Package <| title == 'luke' |> {
}