include abc
include abc, def        # Noncompliant
include abc, def, ghi   # Noncompliant

include Class['base::linux']
include Class['base::linux'], Class['base::tomcat']   # Noncompliant

include( "stdlib", "apache" ) # Noncompliant
