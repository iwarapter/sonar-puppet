if condition1 {          # Compliant - depth = 1
# ...
  if condition2 {        # Compliant - depth = 2
  # ...
    if condition3 {      # Compliant - depth = 3, not exceeding the limit
    # ...
      if condition4 {    # Non-Compliant - depth = 4
        if condition5 {  # Depth = 5, exceeding the limit, but issues are only reported on depth = 4
        # ...
        }
      }
    }
  }
}