#this class installs ntp
class ntp(
  $server = $ntp::params::server
) inherits ntp::params { }