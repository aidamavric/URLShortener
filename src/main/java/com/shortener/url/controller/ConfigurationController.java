package com.shortener.url.controller;

import com.shortener.url.dto.AccountRegistrationRequest;
import com.shortener.url.dto.AccountRegistrationResponse;
import com.shortener.url.dto.UrlRegistrationRequest;
import com.shortener.url.dto.UrlRegistrationResponse;
import com.shortener.url.service.AccountService;
import com.shortener.url.service.UrlService;
import com.shortener.url.util.UrlShortenerConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.Map;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ConfigurationController {

  private final AccountService accountService;
  private final UrlService urlService;

  /**
   * Endpoint for account registration
   *
   * @param accountRegistrationRequest request that contains accountId for registration
   * @return account registration response with status message and description
   */
  @PostMapping(UrlShortenerConstants.REGISTER_ACCOUNT_URL)
  public ResponseEntity<AccountRegistrationResponse> registerAccount(
      @Valid @RequestBody AccountRegistrationRequest accountRegistrationRequest) {
    String accountId = accountRegistrationRequest.getAccountId();
    log.info("Received account registration request for accountId {}", accountId);
    if (accountService.accountExists(accountId)) {
      log.error("Unable to register already existing account {}.", accountId);
      return new ResponseEntity<>(
          AccountRegistrationResponse.builder()
              .success(Boolean.FALSE)
              .description(UrlShortenerConstants.ACCOUNT_ID_ALREADY_EXISTS)
              .build(),
          HttpStatus.CONFLICT);
    }
    String generatedPassword = accountService.saveAccountAndGeneratePassword(accountId);
    log.info("Successfully registered account {}", accountId);
    return new ResponseEntity<>(
        AccountRegistrationResponse.builder()
            .password(generatedPassword)
            .success(Boolean.TRUE)
            .description(UrlShortenerConstants.ACCOUNT_IS_SUCCESSFULLY_CREATED)
            .build(),
        HttpStatus.CREATED);
  }

  /**
   * Endpoint for url registration
   *
   * @param urlRegistrationRequest request that contains url for registration
   * @return shortUrl registration response with generated short url
   */
  @SecurityRequirement(name = UrlShortenerConstants.OPEN_API_BASIC_AUTH)
  @Operation(summary = "Register URL endpoint", security = @SecurityRequirement(name = "basicAuth"))
  @PostMapping(UrlShortenerConstants.REGISTER_URL)
  public ResponseEntity<UrlRegistrationResponse> registerUrl(
      @Valid @RequestBody UrlRegistrationRequest urlRegistrationRequest) {
    log.info("Received url registration request for url {}", urlRegistrationRequest.getUrl());
    String shortUrl = urlService.generateAndSaveUrl(urlRegistrationRequest);
    String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
    log.info("Successfully registered url {}", urlRegistrationRequest.getUrl());
    return new ResponseEntity<>(
        UrlRegistrationResponse.builder()
            .shortUrl(baseUrl + AntPathMatcher.DEFAULT_PATH_SEPARATOR + shortUrl)
            .build(),
        HttpStatus.CREATED);
  }

  /**
   * Endpoint for fetching statistic for one account's url
   *
   * @param accountId id of account to fetch statistic for
   * @return map of account's url and number of visits
   */
  @SecurityRequirement(name = UrlShortenerConstants.OPEN_API_BASIC_AUTH)
  @GetMapping(value = UrlShortenerConstants.GET_STATISTIC_URL)
  public ResponseEntity<Map<String, Long>> getStatistics(
      @PathVariable(value = "AccountId") String accountId) {
    log.info("Received request for getting statistic for account {}", accountId);
    return new ResponseEntity<>(urlService.getStatisticsForAccount(accountId), HttpStatus.OK);
  }
}
