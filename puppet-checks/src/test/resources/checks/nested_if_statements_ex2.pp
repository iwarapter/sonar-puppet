if condition1 {          # Compliant - depth = 1
# ...
  if condition2 {        # Compliant - depth = 2, not exceeding the limit
  # ...
    if condition3 {      # Non-Compliant - depth = 3
    # ...
      if condition4 {    # Depth = 4, exceeding the limit, but issues are only reported on depth = 3
        if condition5 {  # Depth = 5, exceeding the limit, but issues are only reported on depth = 3
        # ...
        }
      }
    }
  }
}