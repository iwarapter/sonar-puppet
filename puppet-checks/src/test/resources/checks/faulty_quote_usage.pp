file { 'foo':
  text => "abc",         # Noncompliant
  text => "$ abc",       # Noncompliant
  text => "${ abc",      # Noncompliant
  text => "${abc}",      # Noncompliant
  text => "$abc",        # Noncompliant
  text => "\n",          # Compliant, contains special character
  text => "abc ${var}",  # Compliant, contains variable
}

file { "abc":            # Noncompliant
}

file { "$abc":           # Noncompliant
}

file { "${abc}":         # Noncompliant
}

file { 'foo':
  text => 'blabla \'blabla\'',  # Noncompliant
}
