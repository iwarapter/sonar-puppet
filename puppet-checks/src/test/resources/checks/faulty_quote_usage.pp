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

file { 'foo':
  text => 'blabla \'blabla\'',  # Noncompliant
}

file { 'foo':
  text => 'blabla
          \'blabla\'',  # Noncompliant
}

# Compliant multiline string
$source = "http://mydomain.com/service//\
          ${group_id_path}/${name}/${version}/${name}-${version}.hpi"

# Noncompliant multiline string, use single quotes instead
$source = "http://mydomain.com/service//\
          abc.hpi"

# Compliant multiline string
$source = "abc\
          d\ne"
