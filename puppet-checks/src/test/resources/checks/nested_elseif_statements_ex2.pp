if condition1 {          # Compliant - depth = 1

} elsif condition2 {
  # ...
  if condition3 {        # Compliant - depth = 2, not exceeding the limit
  # ...
    if condition4 {      # Non-Compliant - depth = 3
    # ...
      if condition5 {    # Depth = 4, exceeding the limit, but issues are only reported on depth = 3
        if condition6 {  # Depth = 5, exceeding the limit, but issues are only reported on depth = 3
        # ...
        }
      }
    }
  }
}