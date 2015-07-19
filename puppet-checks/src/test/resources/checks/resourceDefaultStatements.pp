Exec {
  path        => '/usr/bin:/bin:/usr/sbin:/sbin',
  environment => 'RUBYLIB=/opt/puppet/lib/ruby/site_ruby/1.8/',
  logoutput   => true,
  timeout     => 180,
}

File {
  owner => 'root',
  group => '0',
  mode  => '0644',
}
