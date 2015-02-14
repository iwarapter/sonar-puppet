$variable = "this is a string"

user { 'katie':
  ensure => 'present',
  home   => '/home/katie',
  shell  => '/bin/bash'
}