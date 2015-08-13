if $a {}
if $a == false {}  # Noncompliant
if $a == true {}  # Noncompliant

if $a {}
elsif $a {}

if $a == true {}  # Noncompliant
elsif $a == false {}  # Noncompliant

unless $a {}
unless $a == true {}  # Noncompliant
unless $a == false {}  # Noncompliant

if $a or $a == true {}  # Noncompliant