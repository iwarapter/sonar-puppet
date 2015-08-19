if $a {
  if $b {
  # stuff
  }
  if $c {
  # stuff
  }
}

if $a {
  if $b {
  # stuff
  }
  else {
  # stuff
  }
}

if $a {
  if $b { # Noncompliant
  # stuff
  }
}

if $a {
# stuff
}
elsif $b {
  if $c { # Noncompliant
  # stuff
  }
}

if $a {
# stuff
}
elsif $b {
  if $c {
  # stuff
  }
  else{
  # stuff
  }
}

if $a {
  if $b { # Noncompliant
    if $c { # Noncompliant
    # stuff
    }
  }
}

if $a {
  if $b { # Noncompliant
    if $c {
    # stuff
    }
    else {
    # stuff
    }
  }
}

if $a {
  if $b {
  # stuff
  }
  elsif $c {
  # stuff
  }
}

if $a {
  if $b {
  # stuff
  }
  do_something()
}
