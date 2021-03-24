package com.shortener.url.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import com.shortener.url.entity.Account;
import com.shortener.url.repository.AccountRepository;
import java.util.Optional;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Unit tests for AccountService methods */
public class AccountServiceTest {

  private AccountService accountService;
  private AccountRepository accountRepository;

  public static final String TEST_ACCOUNT_ID = "TEST_ACCOUNT_ID";

  @BeforeEach
  public void init() {
    accountRepository = Mockito.mock(AccountRepository.class);
    PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
    accountService = new AccountService(accountRepository, passwordEncoder);
  }

  @Test
  public void shouldReturnIfAccountExists() {
    // given
    when(accountRepository.existsById(TEST_ACCOUNT_ID)).thenReturn(Boolean.TRUE);

    // when/then
    assertThat(accountService.accountExists(TEST_ACCOUNT_ID)).isTrue();
  }

  @Test
  public void shouldNotReturnIfAccountExists_exceptionIsThrown() {
    // given
    when(accountRepository.existsById(TEST_ACCOUNT_ID)).thenThrow(RuntimeException.class);

    // when
    Throwable expectedThrowable =
        catchThrowable(() -> accountService.accountExists(TEST_ACCOUNT_ID));

    // then
    assertThat(expectedThrowable).isInstanceOf(RuntimeException.class);
  }

  @Test
  public void shouldSaveAccountAndGenerateAlphanumericPassword() {
    // given
    given(accountRepository.existsAccountByPassword(any())).willReturn(Boolean.FALSE);
    String alphaNumericPattern = "^[a-zA-Z0-9]{8}$";

    // when
    String expectedPassword = accountService.saveAccountAndGeneratePassword(TEST_ACCOUNT_ID);

    // then
    assertThat(Pattern.matches(alphaNumericPattern, expectedPassword)).isTrue();
  }

  @Test
  public void shouldNotSaveAccountAndGeneratePassword_exceptionIsThrown() {
    // given
    given(accountRepository.existsAccountByPassword(any())).willThrow(RuntimeException.class);

    // when
    Throwable expectedThrowable =
        catchThrowable(() -> accountService.saveAccountAndGeneratePassword(TEST_ACCOUNT_ID));

    // then
    assertThat(expectedThrowable).isInstanceOf(RuntimeException.class);
  }

  @Test
  public void shouldReturnAccountByAccountId() {
    // given
    Account expectedAccount = new Account(TEST_ACCOUNT_ID, "testPassword");
    given(accountRepository.findById(TEST_ACCOUNT_ID)).willReturn(Optional.of(expectedAccount));

    // when
    Optional<Account> actualAccount = accountService.getAccountByAccountId(TEST_ACCOUNT_ID);

    // then
    assertThat(actualAccount.isPresent()).isTrue();
    assertThat(actualAccount.get()).isEqualTo(expectedAccount);
  }

  @Test
  public void shouldNotReturnAccountByAccountId_exceptionIsThrown() {
    // given
    given(accountRepository.findById(TEST_ACCOUNT_ID)).willThrow(RuntimeException.class);

    // when
    Throwable expectedThrowable =
        catchThrowable(() -> accountService.getAccountByAccountId(TEST_ACCOUNT_ID));

    // then
    assertThat(expectedThrowable).isInstanceOf(RuntimeException.class);
  }
}
