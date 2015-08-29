if ($a == 1) {}              # Noncompliant
if ($a == 1 or $b == 1) {}   # Noncompliant
if (! defined (File[$a])) {} # Noncompliant

if $a == 1 {}
if $a == 1 or $b == 1 {}
if ($a == 1 and $b == 1) or $c == 1 {}
if ($a == 1 and $b == 1) or ($c == 1  and $d == 1) {}
if ! defined (File[$a]) {}
if !($a == 1) {}
