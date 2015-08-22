if $a {
  $b = 1
} elsif $b {
  $c = 1
} else {
  $d = 1
}

if $a
{            # Noncompliant
  $b = 1
} elsif $b
{            # Noncompliant
  $c = 1
} else
{            # Noncompliant
  $d = 1
}

if $a {
  $b = 1
}
elsif $b {   # Noncompliant
  $c = 1
}
else {       # Noncompliant
  $d = 1
}

if $a { $b = 1   # Noncompliant
}

if $a {
  $b = 1
} elsif $b { $c = 1  # Noncompliant
}

if $a {
  $b = 1
} elsif $b {
  $c = 1
} else { $d = 1  # Noncompliant
}

if $a {
  $b = 1 } elsif $b {  # Noncompliant
  $c = 1 } else {      # Noncompliant
  $d = 1 }             # Noncompliant

if $a {
  $b = 1
} elsif $b {
  if $a {
    $b =1
  } else {
    $c = 1
  }
} else {
  $d = 1
}

if $a {
  $b = 1
} elsif $b {
  if $a {
    $b =1
  } else {
    $c = 1
  }
}           # Noncompliant
else {
  $d = 1
}

if $a {
  $b = 1
} elsif $b {
  $b = 1
} elsif $c {
  $b = 1
} else {
  $d = 1
}
