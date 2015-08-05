$rootgroup = $osfamily ? {
  'Solaris' => 'wheel',
  default   => 'root',
}

$rootgroup = $osfamily ? {
  'Solaris' => 'wheel',
}
