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
