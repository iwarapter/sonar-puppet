if condition1 {          # Compliant - depth = 1

} else {
  # ...
  if condition2 {        # Compliant - depth = 2, not exceeding the limit
  # ...
    if condition3 {      # Compliant - depth = 3, not exceeding the limit
    # ...
      if condition4 {    # Noncompliant Depth = 4
        if condition5 {  # Depth = 5, exceeding the limit, but issues are only reported on depth = 3
        # ...
        }
      }
    }
  }
}

if condition1 {          # Compliant - depth = 1
# ...
  if condition2 {        # Compliant - depth = 2, not exceeding the limit
  # ...
    if condition3 { # Compliant - depth = 3, not exceeding the limit
    # ...
    } elsif condition4 {    # Noncompliant Depth = 4
      if condition5 {  # Depth = 5, exceeding the limit, but issues are only reported on depth = 3
      # ...
      }
    }
  }
}