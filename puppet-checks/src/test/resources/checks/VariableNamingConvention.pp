file { $abc:
  ensure => file,
  mode   => $def,
}

file { $Abc:
  ensure => file,
  mode   => $dEf,
}

file { $abc:
  ensure => file,
  mode   => $module::var,
}

file { $abc:
  ensure => file,
  mode   => $::var,
}
