class apache {
  class ssl { } # non compliant
}

# or

class apache {
  define config() { } # non compliant
}

define apache {
  class ssl { } # non compliant
}

define apache {
  define config() { # non compliant
    define config2() { } # non compliant
  }
}

class without {

}

class bar {
  class { 'foo':
    bar => 'foobar'
  }
}
