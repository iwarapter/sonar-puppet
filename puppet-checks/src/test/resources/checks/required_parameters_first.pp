class ntp (
  $options   = "iburst",
  $servers,              # Noncompliant
  $multicast = false
) {}

class ntp (
  $servers,
  $options   = "iburst",
  $multicast = false
) {}

class ntp (
  $servers,
  $options   = "iburst",
  $test,                 # Noncompliant
  $multicast = false
) {}