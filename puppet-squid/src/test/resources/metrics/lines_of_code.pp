#example puppet file

$variable = "this is a string"

user { 'katie':
  ensure => 'present',
  home   => '/home/katie',  #comment on code line
  shell  => '/bin/bash'
}