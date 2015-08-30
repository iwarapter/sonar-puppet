case $operatingsystem {
  'Solaris':          { include role::solaris }
  'RedHat', 'CentOS': { include role::redhat }
  default:            {}
}
case $operatingsystem {
  'Solaris':          { include role::solaris }
  'RedHat', 'CentOS': { include role::redhat }
  default:            {
  # Do nothing because...
  }
}

case $operatingsystem {
  'Solaris':          { include role::solaris }
  'RedHat', 'CentOS': { include role::redhat }
  default:            { /* Do nothing because... */ }
}

if $a {}
elsif $a {}
else {}

if $a {
  $b = 1
}
elsif $a {
  $b = 1
}
else {
  $b = 1
}

if $a {
  # Do nothing because...
}
elsif $a {
  # Do nothing because...
}
else {
  # Do nothing because...
}

unless $a {}
unless $a {
  $b = 1
}
unless $a {
  # Do nothing
}

class abc() {
  $a = 1
}
define abc() {
  $a = 1
}

class abc($a) {
  $a = 1
}
define abc($a) {
  $a = 1
}

class abc {}
define abc {}

file { 'abc': }
File {}
File['abc'] {}

File {
  ensure => present,
}
File['abc'] {
  ensure => absent,
}
