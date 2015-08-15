if $var1 > $var1 or $var1 > $var1 or $var1 > $var1 or $var1 > $var1 {}
if $var1 > $var1 or $var1 > $var1 or $var1 > $var1 or $var1 > $var1 or $var1 > $var1 {}  # Noncompliant

if $var1 > $var1 and $var1 > $var1 and $var1 > $var1 and $var1 > $var1 {}
if $var1 > $var1 and $var1 > $var1 and $var1 > $var1 and $var1 > $var1 and $var1 > $var1 {}  # Noncompliant

if $var1 > $var1 and $var1 > $var1 or $var1 > $var1 and $var1 > $var1 {}
if $var1 > $var1 and $var1 > $var1 or $var1 > $var1 or $var1 > $var1 and $var1 > $var1 {}  # Noncompliant

if ($var1 > $var1 and $var1 > $var1 or $var1 > $var1) or ($var1 > $var1 and $var1 > $var1) {}  # Noncompliant

unless ($var1 > $var1 and $var1 > $var1 or $var1 > $var1) or ($var1 > $var1 and $var1 > $var1) or $var1 > $var1 {}  # Noncompliant

if $var {}
if $var > 1 {}