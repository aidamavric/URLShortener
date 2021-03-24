package com.shortener.url.service;

import com.shortener.url.dto.UserAccountDetails;
import com.shortener.url.entity.Account;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Slf4j
@RequiredArgsConstructor
public class AuthenticationService implements UserDetailsService {

  private final AccountService accountService;

  /**
   * Custom implementation of Spring Security's UserDetailsService's method for obtaining account
   * from DB
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<Account> accountByAccountId = accountService.getAccountByAccountId(username);
    if (accountByAccountId.isPresent()) {
      return new UserAccountDetails(
          accountByAccountId.get().getAccountId(), accountByAccountId.get().getPassword());
    } else {
      log.error("Account with provided accountId {} does not exist", username);
      throw new UsernameNotFoundException("Account with provided accountId does not exist.");
    }
  }
}
