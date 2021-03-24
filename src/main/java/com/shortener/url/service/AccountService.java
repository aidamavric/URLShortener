package com.shortener.url.service;

import com.shortener.url.entity.Account;
import com.shortener.url.repository.AccountRepository;
import java.security.SecureRandom;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

  private static final int PASSWORD_LENGTH = 8;

  private final AccountRepository accountRepository;
  private final PasswordEncoder passwordEncoder;

  /**
   * Checks if account exists by accountId
   *
   * @param accountId
   * @return true if account exists, false otherwise
   */
  @Transactional(readOnly = true)
  public boolean accountExists(String accountId) {
    return accountRepository.existsById(accountId);
  }

  /**
   * Generates password and saves Account for a given accountId
   *
   * @param accountId id of account to generate password for
   * @return generated alphanumeric password for new Account
   */
  @Transactional
  public String saveAccountAndGeneratePassword(String accountId) {
    String password;
    do {
      password =
          RandomStringUtils.random(PASSWORD_LENGTH, 0, 0, true, true, null, new SecureRandom());
    } while (accountRepository.existsAccountByPassword(password));
    accountRepository.save(new Account(accountId, passwordEncoder.encode(password)));
    return password;
  }

  /**
   * Fetches Account by accountId
   *
   * @param accountId
   * @return Account object or empty
   */
  @Transactional(readOnly = true)
  public Optional<Account> getAccountByAccountId(String accountId) {
    return accountRepository.findById(accountId);
  }
}
