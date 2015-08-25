user { 'tim':
  ensure => present
}

user { $username:
  ensure => present
}

user {
  $username :
    ensure => present;

  "tim" :
    ensure => present;
}