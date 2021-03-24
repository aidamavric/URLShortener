package com.shortener.url.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

import com.shortener.url.UrlShortenerApplication;
import com.shortener.url.entity.Url;
import com.shortener.url.service.UrlService;
import java.util.Optional;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;

/** Integration tests for UrlShortenerController endpoint */
@SpringBootTest(
    classes = UrlShortenerApplication.class,
    webEnvironment = WebEnvironment.RANDOM_PORT)
class UrlShortenerControllerTest {

  @LocalServerPort private int port;

  @MockBean private UrlService urlService;

  @Autowired private TestRestTemplate restTemplate;

  private static final String LOCALHOST = "http://localhost:";
  private static final String TEST_URL = "https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html";

  @Test
  public void shouldRedirectSuccessfully() {
    // given
    String testShortUrl = RandomStringUtils.randomAlphanumeric(8);
    Url expectedUrl = new Url();
    expectedUrl.setShortUrl(testShortUrl);
    int redirectionType = 302;
    expectedUrl.setRedirectType(redirectionType);
    expectedUrl.setOriginalUrl(TEST_URL);

    given(urlService.getUrlByShortUrl(testShortUrl)).willReturn(Optional.of(expectedUrl));

    // when
    ResponseEntity<String> actualResponse =
        restTemplate.getForEntity(
            LOCALHOST + port + AntPathMatcher.DEFAULT_PATH_SEPARATOR + testShortUrl, String.class);

    // then
    assertThat(actualResponse.getStatusCode()).isEqualTo(HttpStatus.valueOf(redirectionType));
    assertThat(actualResponse.getHeaders().getLocation().toString())
        .isEqualTo(expectedUrl.getOriginalUrl());
  }

  @Test
  public void shouldNotRedirectNotAlphanumericUrl() {
    // given
    String testShortUrl = "!@Test122324";

    // when
    ResponseEntity<String> actualResponse =
        restTemplate.getForEntity(
            LOCALHOST + port + AntPathMatcher.DEFAULT_PATH_SEPARATOR + testShortUrl, String.class);

    // then
    assertThat(actualResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  public void shouldNotRedirectNotExistingUrl() {
    // given
    String testShortUrl = RandomStringUtils.randomAlphanumeric(8);
    given(urlService.getUrlByShortUrl(testShortUrl)).willReturn(Optional.empty());

    // when
    ResponseEntity<String> actualResponse =
        restTemplate.getForEntity(
            LOCALHOST + port + AntPathMatcher.DEFAULT_PATH_SEPARATOR + testShortUrl, String.class);

    // then
    assertThat(actualResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  public void shouldNotRedirectUrl_internalServerError() {
    // given
    String testShortUrl = RandomStringUtils.randomAlphanumeric(8);
    given(urlService.getUrlByShortUrl(testShortUrl)).willThrow(RuntimeException.class);

    // when
    ResponseEntity<String> actualResponse =
        restTemplate.getForEntity(
            LOCALHOST + port + AntPathMatcher.DEFAULT_PATH_SEPARATOR + testShortUrl, String.class);

    // then
    assertThat(actualResponse.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
