package com.shortener.url.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import com.shortener.url.UrlShortenerApplication;
import com.shortener.url.controller.config.SecurityTestConfig;
import com.shortener.url.dto.AccountRegistrationRequest;
import com.shortener.url.dto.AccountRegistrationResponse;
import com.shortener.url.dto.UrlRegistrationRequest;
import com.shortener.url.service.AccountService;
import com.shortener.url.service.UrlService;
import com.shortener.url.util.UrlShortenerConstants;
import java.util.HashMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/** Integration tests for ConfigurationController endpoints */
@SpringBootTest(
    classes = {UrlShortenerApplication.class, SecurityTestConfig.class},
    webEnvironment = WebEnvironment.RANDOM_PORT)
public class ConfigurationControllerTest {

  @LocalServerPort private int port;

  @MockBean private AccountService accountService;
  @MockBean private UrlService urlService;

  @Autowired private TestRestTemplate restTemplate;

  private static final String LOCALHOST = "http://localhost:";
  private static final String TEST_URL = "https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html";
  private static final String TEST_ACCOUNT_ID = "TEST";

  @Test
  public void shouldRegisterAccountSuccessfully() {
    // given
    String testAccountId = TEST_ACCOUNT_ID;
    AccountRegistrationRequest accountRegistrationRequest = new AccountRegistrationRequest();
    accountRegistrationRequest.setAccountId(testAccountId);

    when(accountService.accountExists(testAccountId)).thenReturn(Boolean.FALSE);
    String randomPassword = "RANDOM_PASSWORD";
    when(accountService.saveAccountAndGeneratePassword(testAccountId)).thenReturn(randomPassword);
    AccountRegistrationResponse expectedResponse =
        AccountRegistrationResponse.builder()
            .success(Boolean.TRUE)
            .description(UrlShortenerConstants.ACCOUNT_IS_SUCCESSFULLY_CREATED)
            .password(randomPassword)
            .build();

    // when
    ResponseEntity<AccountRegistrationResponse> actualResponse =
        restTemplate.postForEntity(
            LOCALHOST + port + UrlShortenerConstants.REGISTER_ACCOUNT_URL,
            accountRegistrationRequest,
            AccountRegistrationResponse.class);

    // then
    assertThat(actualResponse.getBody()).isEqualTo(expectedResponse);
    assertThat(actualResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
  }

  @Test
  public void shouldNotRegisterAccount_accountIdAlreadyExists() {
    // given
    String testAccountId = TEST_ACCOUNT_ID;
    AccountRegistrationRequest accountRegistrationRequest = new AccountRegistrationRequest();
    accountRegistrationRequest.setAccountId(testAccountId);

    when(accountService.accountExists(testAccountId)).thenReturn(Boolean.TRUE);
    AccountRegistrationResponse expectedResponse =
        AccountRegistrationResponse.builder()
            .success(Boolean.FALSE)
            .description(UrlShortenerConstants.ACCOUNT_ID_ALREADY_EXISTS)
            .build();

    // when
    ResponseEntity<AccountRegistrationResponse> actualResponse =
        restTemplate.postForEntity(
            LOCALHOST + port + UrlShortenerConstants.REGISTER_ACCOUNT_URL,
            accountRegistrationRequest,
            AccountRegistrationResponse.class);

    // then
    assertThat(actualResponse.getBody()).isEqualTo(expectedResponse);
    assertThat(actualResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  public void shouldNotRegisterEmptyAccount() {
    // given
    AccountRegistrationRequest accountRegistrationRequest = new AccountRegistrationRequest();

    // when
    ResponseEntity<String> actualResponse =
        restTemplate.postForEntity(
            LOCALHOST + port + UrlShortenerConstants.REGISTER_ACCOUNT_URL,
            accountRegistrationRequest,
            String.class);

    // then
    assertThat(actualResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(actualResponse.getBody())
        .contains(UrlShortenerConstants.ACCOUNT_ID_MUST_NOT_BE_EMPTY_OR_NULL);
  }

  @Test
  public void shouldNotRegisterAccount_internalServerError() {
    // given
    String testAccountId = TEST_ACCOUNT_ID;
    AccountRegistrationRequest accountRegistrationRequest = new AccountRegistrationRequest();
    accountRegistrationRequest.setAccountId(testAccountId);

    when(accountService.accountExists(testAccountId)).thenThrow(RuntimeException.class);

    // when
    ResponseEntity<AccountRegistrationResponse> actualResponse =
        restTemplate.postForEntity(
            LOCALHOST + port + UrlShortenerConstants.REGISTER_ACCOUNT_URL,
            accountRegistrationRequest,
            AccountRegistrationResponse.class);

    // then
    assertThat(actualResponse.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  public void shouldRegisterUrl() {
    // given
    UrlRegistrationRequest urlRegistrationRequest = new UrlRegistrationRequest();
    urlRegistrationRequest.setUrl(TEST_URL);
    urlRegistrationRequest.setRedirectType(301);

    when(urlService.generateAndSaveUrl(urlRegistrationRequest)).thenReturn("randomUrl");

    // when
    ResponseEntity<String> actualResponse =
        restTemplate.postForEntity(
            LOCALHOST + port + UrlShortenerConstants.REGISTER_URL,
            urlRegistrationRequest,
            String.class);

    // then
    assertThat(actualResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
  }

  @Test
  public void shouldNotRegisterEmptyUrl() {
    // given
    UrlRegistrationRequest urlRegistrationRequest = new UrlRegistrationRequest();

    // when
    ResponseEntity<String> actualResponse =
        restTemplate.postForEntity(
            LOCALHOST + port + UrlShortenerConstants.REGISTER_URL,
            urlRegistrationRequest,
            String.class);

    // then
    assertThat(actualResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(actualResponse.getBody())
        .contains(UrlShortenerConstants.URL_MUST_NOT_BE_EMPTY_OR_NULL);
  }

  @Test
  public void shouldNotRegisterBadSyntaxUrl() {
    // given
    UrlRegistrationRequest urlRegistrationRequest = new UrlRegistrationRequest();
    urlRegistrationRequest.setUrl("//badUrl");

    // when
    ResponseEntity<String> actualResponse =
        restTemplate.postForEntity(
            LOCALHOST + port + UrlShortenerConstants.REGISTER_URL,
            urlRegistrationRequest,
            String.class);

    // then
    assertThat(actualResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(actualResponse.getBody())
        .contains(UrlShortenerConstants.URL_MUST_BE_VALID_URL_SYNTAX);
  }

  @Test
  public void shouldNotRegisterBadRedirectTypeUrl() {
    // given
    UrlRegistrationRequest urlRegistrationRequest = new UrlRegistrationRequest();
    urlRegistrationRequest.setUrl(TEST_URL);
    urlRegistrationRequest.setRedirectType(300);

    // when
    ResponseEntity<String> actualResponse =
        restTemplate.postForEntity(
            LOCALHOST + port + UrlShortenerConstants.REGISTER_URL,
            urlRegistrationRequest,
            String.class);

    // then
    assertThat(actualResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(actualResponse.getBody())
        .contains(UrlShortenerConstants.SHOULD_HAVE_ON_OF_FOLLOWING_VALUES_301_OR_302);
  }

  @Test
  public void shouldNotRegisterUrl_internalServerError() {
    // given
    UrlRegistrationRequest urlRegistrationRequest = new UrlRegistrationRequest();
    urlRegistrationRequest.setUrl(TEST_URL);
    urlRegistrationRequest.setRedirectType(301);
    given(urlService.generateAndSaveUrl(urlRegistrationRequest)).willThrow(RuntimeException.class);

    // when
    ResponseEntity<String> actualResponse =
        restTemplate.postForEntity(
            LOCALHOST + port + UrlShortenerConstants.REGISTER_URL,
            urlRegistrationRequest,
            String.class);

    // then
    assertThat(actualResponse.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  public void shouldGetStatistic() {
    // given
    given(urlService.getStatisticsForAccount(TEST_ACCOUNT_ID)).willReturn(new HashMap<>());

    // when
    ResponseEntity<String> actualResponse =
        restTemplate.getForEntity(
            LOCALHOST + port + UrlShortenerConstants.GET_STATISTIC_URL,
            String.class,
            TEST_ACCOUNT_ID);

    // then
    assertThat(actualResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  public void shouldNotGetStatistic_internalServerError() {
    // given
    given(urlService.getStatisticsForAccount(TEST_ACCOUNT_ID)).willThrow(RuntimeException.class);

    // when
    ResponseEntity<String> actualResponse =
        restTemplate.getForEntity(
            LOCALHOST + port + UrlShortenerConstants.GET_STATISTIC_URL,
            String.class,
            TEST_ACCOUNT_ID);

    // then
    assertThat(actualResponse.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
