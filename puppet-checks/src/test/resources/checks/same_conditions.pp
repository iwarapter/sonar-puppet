$param = 2

if $param == 1{
  # stuff
}
elsif $param == 2{
  # stuff
}
elsif $param == 1{ # Noncompliant
  # stuff
}

if $param == 1{
  # stuff
}
else{
  if $param == 2{
    # stuff
  }
  else {
    if $param == 2{ # Noncompliant
      # stuff
    }
  }
}

if $param == 1{
  # stuff
}
else{
  if $param == 2{
    # stuff
  }
  elsif $param == 1{ # Noncompliant
    # stuff
  }
}


if $param == 1{
  # stuff
}
else{
  # stuff
  do_something($param)
  if $param == 1{
    # stuff
  }
  elsif $param == 1{ # Noncompliant
    # stuff
    # stuff
  }
}

if $param == 1{
  if $param > 0{
    # stuff
  }
}
elsif $param == 2{
  if $param > 0{ # Compliant
    # stuff
  }
}
elsif $param == 3{
  if $param > 0{ # Compliant
    # stuff
  }
}

if $param == 1 and $var == 2 {
}
elsif $param == 1 and $var == 2 { # Noncompliant
}

$bool_var = true

if $bool_var {

}
elsif $bool_var {

}

if $param == 1 {

}
else {
  if $bool_var {

  }
  else {
    if $bool_var {

    }
  }
}

if $regex_var =~ /words/ {

}
else {
  if $regex_var =~ /words/ {

  }
}

case $param {
  5, 6: {
  #stuff
  }
  5: { # Noncompliant
  #stuff
  }
  6: { # Noncompliant
  #stuff
  }
  default: {
  #stuff
  }
}

case $param {
  /^(Debian|Ubuntu)$/: {
  #stuff
  }
  /^(Debian|Ubuntu)$/: { # Noncompliant
  #stuff
  }
  /^(Debian|Ubuntu)$/: { # Noncompliant
  #stuff
  }
  default: {
  #stuff
  }
}

$rootgroup = $osfamily ? {
  'Solaris'          => 'wheel',
  /(Darwin|FreeBSD)/ => 'wheel',
  'Solaris'          => 'abc',     # Noncompliant
  /(Darwin|FreeBSD)/ => 'abc',     # Noncompliant
  default            => 'root',
}
