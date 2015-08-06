# Compliant block of code
package { 'openssh-server':
  ensure => present,
}
->
file { '/etc/ssh/sshd_config':
  ensure => file,
  mode   => '0600',
  source => 'puppet:///modules/sshd/sshd_config',
}
~>
service { 'sshd':
  ensure => running,
  enable => true,
}

#Noncompliant block of code
service { 'sshd':
  ensure => running,
  enable => true,
}
<~
file { '/etc/ssh/sshd_config':
  ensure => file,
  mode   => '0600',
  source => 'puppet:///modules/sshd/sshd_config',
}
<-
package { 'openssh-server':
  ensure => present,
}
