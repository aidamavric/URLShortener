package com.shortener.url.dto;

import java.util.Collection;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * UserDetails custom implementation provides to Spring Security information required for basic
 * authentication process
 */
@AllArgsConstructor
public class UserAccountDetails implements UserDetails {

  private final String accountId;
  private final String password;

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return accountId;
  }

  /**
   * Rest of the methods are not applicable for this use-case since there are no authorities and
   * etc, therefore default values are provided.
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return null;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
