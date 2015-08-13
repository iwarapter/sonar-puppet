# comment
if (1 == 1) {}

# Noncompliant
# if (1 == 1) {}

# Noncompliant
/* if (1 == 1) {} */

# Noncompliant
/*
file {'/tmp/junk':
  owner => root,
}
*/

/* this is an example comment */

/*
  Another comment across multilines
*/

# Noncompliant
# file { '/tmp/readme.txt':
#   ensure => file,
#   mode   => '0644',
# }
