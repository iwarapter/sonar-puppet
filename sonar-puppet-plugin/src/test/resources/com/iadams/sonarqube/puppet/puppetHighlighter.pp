# Comment
# Comment 2

$variable = "this is a string"
$variable2 = "this is a string"

class ssh::client inherits workstation { }

class wordpress inherits apache { }

file { '/tmp/foo':
  purge => 'true',
}