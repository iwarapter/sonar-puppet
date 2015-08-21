node server1 { # noncompliant
}

node 'server2' {
}

node 'www1.example.com', 'www2.example.com', 'www3.example.com' {
  include common
  include apache, squid
}

node example4, example5, example6 { # noncompliant (3 times)
  include common
  include apache, squid
}

node /^(foo|bar)\.example\.com$/ {
  include common
}

node /^www\d+$/ {
  include common
}

node default {
  include common
}

node 'www1.example.com' inherits example {
  include common
}