class foo::bar inherits foo { }

class foo::bar inherits foo::baz { }

class foo::bar inherits baz { } # Noncompliant

class ssh::server::solaris inherits ssh::server { }

class abc {}
