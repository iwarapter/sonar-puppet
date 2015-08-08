$my_hash = {
}

$my_hash = {
  key1 => 'value 1',
}

$my_hash = {
  key1 => 'value 1',
  key2 => 'value 2',
}

$my_hash = {
  key1 => 'value 1',
  key2 => 'value 2',
  key1 => 'value 3', # Noncompliant
}

$my_hash = {
  key1   => 'value 1',
  key2   => 'value 2',
  'key1' => 'value 3',
}

$my_hash = {
  'key1' => 'value 1',
  'key2' => 'value 2',
  'key1' => 'value 3', # Noncompliant
}