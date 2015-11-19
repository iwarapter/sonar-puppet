class abc (
  $a = 'abc',
 $b,  # Noncompliant
   $c,  # Noncompliant
) {
  if $a {
    $b = 1
     $c = 1  # Noncompliant
   $d = 1  # Noncompliant
  }
  if $a {
    $b = 1
     $c = 1  # Noncompliant
   $d = 1  # Noncompliant
  }
}

class abc (
  $a = true,
  $b = undef,
  $c = undef,
) {
  if $template {
    if $content {
        warning('aaaa') # Noncompliant
      }
      $a = template($template)   # Noncompliant
    }
    elsif $content {
      $b = $content   # Noncompliant
    }
    else {
      $c = template('abc')  # Noncompliant
    }
}

unless $memorysize > 1024 {
  $maxclient = 500
    $maxclient = 500  # Noncompliant
}

case $operatingsystem {
  'Solaris':          { include role::solaris }
    'RedHat', 'CentOS': { include role::redhat  }  # Noncompliant
  /^(Debian|Ubuntu)$/: {
      include role::debian  # Noncompliant
  }
}

$rootgroup = $osfamily ? {
  'Solaris'          => 'wheel',
    /(Darwin|FreeBSD)/ => 'wheel',  # Noncompliant
  default            => 'root',
}

define abc (
  $a = 'abc',
    $b,  # Noncompliant
 $c,  # Noncompliant
) {
  if $a {
    $b = 1
     $c = 1  # Noncompliant
   $d = 1  # Noncompliant
  }
}

file { 'abc':
  ensure => present,
   ensure => present,  # Noncompliant
 ensure => present,   # Noncompliant
}

file {
  'abc':
    ensure => present,
     ensure => present,  # Noncompliant
   ensure => present;    # Noncompliant
    'abc':               # Noncompliant
    ensure => present,
    ensure => present,
    ensure => present;
}

File {
  ensure => present,
    ensure => present,  # Noncompliant
 ensure => present,   # Noncompliant
}

File['abc'] {
  ensure => present,
   ensure => present,  # Noncompliant
 ensure => present,   # Noncompliant
}

File['abc'] {
  ensure => $osfamily ? {
    'Solaris'          => 'wheel',
      /(Darwin|FreeBSD)/ => 'wheel',  # Noncompliant
    default            => 'root',
  },
    ensure => present,   # Noncompliant
}

node 'www1.example.com' {
  include common
    include apache  # Noncompliant
 include squid      # Noncompliant
}

$a = {
  'a' => $b,
    'a' => $b,  # Noncompliant
  'a' => $b ? {
    true    => $::a,
      default => undef,  # Noncompliant
  },
}

class abc {
  $a = ['a', 'b', 'c']
  $a = [
    'a',
   'b',   # Noncompliant
     'c', # Noncompliant
  ]

  $a = [
    {
      'a' => 'a',
       'a' => 'a',  # Noncompliant
     'a' => 'a',    # Noncompliant
    },
    'b',
     'c',   # Noncompliant
   'd',     # Noncompliant
  ]
}

  class abc {}     # Noncompliant
  $a = 'r'         # Noncompliant
  $a = ['a', 'b']  # Noncompliant


class abc (
  $a = 'abc',
  $b,
) {
  if $a {
    $b = 1
    $c = 1
    $d = 1
  }
  if $a {
    $b = 1
    $c = 1
    $d = 1
  }
}

class abc (
  $a = true,
  $b = undef,
  $c = undef,
) {
  if $template {
    if $content {
      warning('aaaa')
    }
    $a = template($template)
  }
  elsif $content {
    $b = $content
  }
  else {
    $c = template('abc')
  }
}

unless $memorysize > 1024 {
  $maxclient = 500
  $maxclient = 500
}

case $operatingsystem {
  'Solaris':          { include role::solaris }
  'RedHat', 'CentOS': { include role::redhat  }
  /^(Debian|Ubuntu)$/: {
    include role::debian
  }
}

$rootgroup = $osfamily ? {
  'Solaris'          => 'wheel',
  /(Darwin|FreeBSD)/ => 'wheel',
  default            => 'root',
}

define abc (
  $a = 'abc',
  $b,
  $c,
) {
  if $a {
    $b = 1
    $c = 1
    $d = 1
  }
}

file { 'abc':
  ensure => present,
  ensure => present,
  ensure => present,
}


file {
  'abc':
    ensure => present,
    ensure => present,
    ensure => present;
  'abc':
    ensure => present,
    ensure => present,
    ensure => present;
}

File {
  ensure => present,
  ensure => present,
  ensure => present,
}

File['abc'] {
  ensure => present,
  ensure => present,
  ensure => present,
}


File['abc'] {
  ensure => $osfamily ? {
    'Solaris'          => 'wheel',
    /(Darwin|FreeBSD)/ => 'wheel',
    default            => 'root',
  },
  ensure => present,
}

node 'www1.example.com' {
  include common
  include apache
  include squid
}

$a = {
  'a' => $b,
  'a' => $b,
  'a' => $b ? {
    true    => $::a,
    default => undef,
  },
}


class abc {
  $a = ['a', 'b', 'c']
  $a = [
    'a',
    'b',
    'c',
  ]

  $a = [
    {
      'a' => 'a',
      'a' => 'a',
      'a' => 'a',
    },
    'b',
    'c',
    'd',
  ]
}

class abc {}
$a = 'r'
$a = ['a', 'b']

file { '/tmp/readme.txt':
   ensure => file, # Noncompliant (because of the three spaces instead of two)
   owner  => '0', # Noncompliant - issue raised above
   group  => '0', # Noncompliant - issue raised above
   mode   => '0644', # Noncompliant - issue raised above
}

file { '/tmp/readme.txt':
   ensure => file, # Noncompliant (because of the three spaces instead of two)
   owner  => '0', # Noncompliant - issue raised above
  group  => '0',
   mode   => '0644', # Noncompliant
}