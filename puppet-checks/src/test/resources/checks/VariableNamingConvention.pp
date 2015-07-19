file { $abc:
  ensure => file,
  mode   => $def,
}

file { $Abc:
  ensure => file,
  mode   => $dEf,
}

file { $abc-def:
  ensure => file,
  mode   => $ghi-jkl,
}
