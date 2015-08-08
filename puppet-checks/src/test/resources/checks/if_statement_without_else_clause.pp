if $is_virtual {                         # Noncompliant
  notice 'virtual'
}
elsif $operatingsystem == 'Windows' {
  notice 'Windows'
}

if $is_virtual {
  notice 'virtual'
}
elsif $operatingsystem == 'Windows' {
  notice 'Windows'
} else {
  # Do nothing because...
}


if $is_virtual {
  notice 'virtual'
}
elsif $operatingsystem == 'Windows' {
  notice 'Windows'
  if $is_abc {                         #Noncompliant
    notice 'abc'
  } elsif $is_def {
    notice 'def'
  }
} else {
  # Do nothing because...
}