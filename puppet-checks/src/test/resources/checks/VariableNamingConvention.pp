file { $abc:
  ensure => file,
  mode   => $def,
}

file { $Abc:       # Noncompliant
  ensure => file,
  mode   => $dEf,  # Noncompliant
}

file { $abc:
  ensure => file,
  mode   => $module::var,
}

file { $abc:
  ensure => file,
  mode   => $::var,
}

file { $abc:
  ensure => file,
  mode   => $::module::var,
}

file { $abc:
  ensure => file,
  mode   => $::module::_var,
}

file { $abc:
  ensure => file,
  mode   => $::_module::var,  # Noncompliant
}

