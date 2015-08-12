warning "$abc"  # Noncompliant
warning "${abc}"
warning "${abc }"
warning "$ abc"

warning "${access_log_tmp} ${format_log}"
warning "${access_log_tmp} $format_log"     # Noncompliant
