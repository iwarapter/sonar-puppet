file { '/etc/apache/apache2.conf':
  source => 'puppet:///apache/etc/apache/apache2.conf',  # Noncompliant
}

file { '/etc/apache/apache2.conf':
  source => "puppet:///apache/etc/apache/apache2.conf",  # Noncompliant
}

file { '/etc/apache/apache2.conf':
  source => "puppet:///apache/etc/apache/${file}",       # Noncompliant
}

file { '/etc/apache/apache2.conf':
  source => "${var1}/${var2}",
}

File {
  source => 'puppet:///apache/etc/apache/apache2.conf',  # Noncompliant
}

File {
  source => "puppet:///apache/etc/apache/apache2.conf",  # Noncompliant
}

File {
  source => "puppet:///apache/etc/apache/${file}",       # Noncompliant
}

File {
  source => "${var1}/${var2}",
}

File['/etc/apache/apache2.conf'] {
  source => 'puppet:///apache/etc/apache/apache2.conf',  # Noncompliant
}

File['/etc/apache/apache2.conf'] {
  source => "puppet:///apache/etc/apache/apache2.conf",  # Noncompliant
}

File['/etc/apache/apache2.conf'] {
  source => "puppet:///apache/etc/apache/${file}",       # Noncompliant
}

File['/etc/apache/apache2.conf'] {
  source => "${var1}/${var2}",
}

file { '/etc/apache/apache2.conf':
  abc => 'puppet:///apache2/etc/apache/apache2.conf',    # Noncompliant
}

file { '/etc/apache/apache2.conf':
  source => 'puppet:///modules/apache2/etc/apache/apache2.conf',
}

File {
  source => 'puppet:///modules/apache2/etc/apache/apache2.conf',
}

File['/etc/apache/apache2.conf'] {
  source => 'puppet:///modules/apache2/etc/apache/apache2.conf',
}

file { '/etc/apache/apache2.conf':
}

File {
}

File['/etc/apache/apache2.conf'] {
}

file { '/etc/apache/apache2.conf':
  source => "puppet:///${var}",
}

$source = "puppet:///apache2/etc/apache/apache2.conf"  # Noncompliant
$source = "puppet:///${var}/etc/apache/apache2.conf"
$source = "puppet:///company-foo/apache2/etc/apache/apache2.conf"  # Noncompliant when no custom

file { '/etc/apache/apache2.conf':
  source => $source,
}