file { '/tmp/readme.txt':
  mode => $::operatingsystem ? {
    debian => '0777',
    redhat => '0776',
    fedora => '0007',
  }
}

$file_mode = $::operatingsystem ? {
  debian => '0007',
  redhat => '0776',
  fedora => '0007',
}

file { '/tmp/readme.txt':
  mode => $file_mode,
}

file { '/tmp/readme.txt':
  mode => $::operatingsystem ? {
    debian => '0007',
    redhat => '0776',
    fedora => '0007',
  },

  group => $::var ? {
    debian => '0777',
    fedora => '0007',
  }
}

File {
  mode => $::operatingsystem ? {
    debian => '0777',
    redhat => '0776',
    fedora => '0007',
  }
}