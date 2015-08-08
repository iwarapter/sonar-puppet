node 'www1.example.com' inherits 'common' {  # Noncompliant
  include ntp
  include apache
}

node 'www1.example.com' {
  include ntp
  include apache
}