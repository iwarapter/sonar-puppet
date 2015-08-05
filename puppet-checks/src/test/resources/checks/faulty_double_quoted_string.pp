file { 'foo':
  text => "abc",     # Noncompliant
  text => "$ abc",   # Noncompliant
  text => "${ abc",  # Noncompliant
  text => "${abc}",  # Noncompliant
  text => "$abc",    # Noncompliant
  text => "\n",
}