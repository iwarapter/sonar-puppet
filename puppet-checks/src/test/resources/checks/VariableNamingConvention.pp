file { $abc:
  ensure => file,
  mode   => $def,
}

file { $Abc:
  ensure => file,
  mode   => $dEf,
}
