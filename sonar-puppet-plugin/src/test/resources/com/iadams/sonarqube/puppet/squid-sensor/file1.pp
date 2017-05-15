# Comment
# Comment 2

$variable = "this is a string"
$variable2 = "this is a string"

class ssh::client inherits workstation { }

class wordpress inherits apache { }

if $var1 > $var1 or $var1 > $var1 or $var1 > $var1 or $var1 > $var1 or $var1 > $var1 {}  # Noncompliant

file { '/tmp/foo':
  purge => 'true',
}

if $param == 1{
  # stuff
}
elsif $param == 2{
  # stuff
}
elsif $param == 1{ # Noncompliant
  # stuff
}

