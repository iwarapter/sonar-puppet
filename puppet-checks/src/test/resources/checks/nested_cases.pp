case $var1 {
  'abc': {
    include pup
  }
  'def', 'ghi': {
    include pup
  }
  default: {
    include pup3
  }
}

case $var1 {
  'abc': {
    include pup
  }
  'def', 'ghi': {
    case $var2 {       # Noncompliant
      'jkl': {
        include pup
      }
      'mno': {
        include pup2
      }
    }
  }
  default: {
    include pup3
  }
}

case $var1 {
  'abc': {
    include pup
  }
  'def', 'ghi': {
    case $var2 {       # Noncompliant
      'jkl': {
        case $var3 {   # Noncompliant
          'jkl': {
            include pup
          }
          'mno': {
            include pup2
          }
        }
      }
      'mno': {
        include pup2
      }
    }
  }
  default: {
    include pup3
  }
}

$answer = $source ? {
  undef   => $content ? {           # Noncompliant
    undef   => template($template),
    default => $content,
    },
  default => undef,
}

case $operatingsystem {
  'Solaris':          {
    $state = $hostname ? {          # Noncompliant
      host1   => 'present',
      host2   => 'present',
      default => 'absent',
    }
  }

  'RedHat', 'CentOS': {
    # do stuff
  }

  default:            {
    # apply the default
  }
}