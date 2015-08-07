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

file { "$abc":           # Compliant because it is a resource title
}

file { "${abc}":         # Compliant because it is a resource title
}