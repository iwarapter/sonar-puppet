class abc {

  if $a or $b { }
  elsif $c { }

  file { 'abc':
    ensure => present,
  }

  File {
    ensure => absent,
  }

  case $c {
    'abc': {}
    'def': {}
    default: {}
  }

  if $d or $z or $r {}
}

define abc {

  if $a or $b { }
  elsif $c { }

  file { 'abc':
    ensure => present,
  }

  File {
    ensure => absent,
  }

  case $c {
    'abc': {}
    'def': {}
    default: {}
  }

  if $d or $z or $r or $t {}
}

define abc {}
class abc {}