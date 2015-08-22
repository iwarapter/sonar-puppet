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