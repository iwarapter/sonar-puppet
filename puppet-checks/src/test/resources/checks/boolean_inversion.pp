if ( !(a == 2)) { }  # Noncompliant
$b = !(i < 10)  # Noncompliant
$b = !(i > 10)  # Noncompliant
$b = !(i != 10)  # Noncompliant
$b = !(i <= 10)  # Noncompliant
$b = !(i >= 10)  # Noncompliant

if (a != 2) { }
$b = (i >= 10)
$b = !(a + i)
$b = !$a

$b = !($a == 1 and $c > 1)  # Noncompliant
$b = !($a == 1 or $c > 1 or $d == 2)  # Noncompliant