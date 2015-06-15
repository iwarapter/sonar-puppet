user { 'nick':
  ensure         => present,
  purge_ssh_keys => true,
}