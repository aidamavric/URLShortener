package com.shortener.url.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import com.shortener.url.dto.UrlRegistrationRequest;
import com.shortener.url.dto.UserAccountDetails;
import com.shortener.url.entity.Account;
import com.shortener.url.entity.Url;
import com.shortener.url.repository.UrlRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/** Unit tests for UrlService methods */
public class UrlServiceTest {

  private UrlRepository urlRepository;
  private UrlService urlService;

  private static final String TEST_ACCOUNT = "TEST_ACCOUNT";

  @BeforeEach
  public void init() {
    urlRepository = Mockito.mock(UrlRepository.class);
    urlService = spy(new UrlService(urlRepository));
    doReturn(new UserAccountDetails(TEST_ACCOUNT, "testPass"))
        .when(urlService)
        .getAuthenticationDetails();
    urlService.setShortUrlLength(String.valueOf(10));
  }

  @Test
  public void shouldGenerateAndSaveAlphanumericUrl() {
    // given
    given(urlRepository.existsById(any())).willReturn(Boolean.FALSE);
    UrlRegistrationRequest urlRegistrationRequest = new UrlRegistrationRequest();
    urlRegistrationRequest.setRedirectType(301);
    urlRegistrationRequest.setUrl("http://google.com/longUrl");
    String alphaNumericPattern = "^[a-zA-Z0-9]{10}$";

    // when
    String expectedShortUrl = urlService.generateAndSaveUrl(urlRegistrationRequest);

    // then
    assertThat(Pattern.matches(alphaNumericPattern, expectedShortUrl)).isTrue();
  }

  @Test
  public void shouldNotGenerateAndSaveUrl_exceptionIsThrown() {
    // given
    given(urlRepository.existsById(any())).willReturn(Boolean.FALSE);
    given(urlRepository.save(any())).willThrow(RuntimeException.class);
    UrlRegistrationRequest urlRegistrationRequest = new UrlRegistrationRequest();
    urlRegistrationRequest.setRedirectType(301);
    urlRegistrationRequest.setUrl("http://google.com/someLongUrl");

    // when
    Throwable expectedThrowable =
        catchThrowable(() -> urlService.generateAndSaveUrl(urlRegistrationRequest));

    // then
    assertThat(expectedThrowable).isInstanceOf(RuntimeException.class);
  }

  @Test
  public void shouldReturnStatisticForAccount() {
    // given
    List<Url> urls = new ArrayList<>();
    String shortUrl = "shortUrl";
    String firstLongUrl = "http://google.com";
    String secondLongUrl = "http://stackOverflow.com";
    String thirdLongUrl = "https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html";
    urls.add(new Url(shortUrl, firstLongUrl, 302, 100L, new Account()));
    urls.add(new Url(shortUrl, secondLongUrl, 302, 30L, new Account()));
    urls.add(new Url(shortUrl, firstLongUrl, 302, 120L, new Account()));
    urls.add(new Url(shortUrl, secondLongUrl, 302, 22L, new Account()));
    urls.add(new Url(shortUrl, thirdLongUrl, 301, 545L, new Account()));
    given(urlRepository.findAllByAccount_AccountId(TEST_ACCOUNT)).willReturn(urls);
    Map<String, Long> expectedResults = new HashMap<>();
    expectedResults.put(firstLongUrl, 220L);
    expectedResults.put(secondLongUrl, 52L);
    expectedResults.put(thirdLongUrl, 545L);

    // when
    Map<String, Long> statistic = urlService.getStatisticsForAccount(TEST_ACCOUNT);

    // then
    assertThat(statistic.size()).isEqualTo(expectedResults.size());
    assertThat(statistic.get(firstLongUrl)).isEqualTo(expectedResults.get(firstLongUrl));
    assertThat(statistic.get(secondLongUrl)).isEqualTo(expectedResults.get(secondLongUrl));
    assertThat(statistic.get(thirdLongUrl)).isEqualTo(expectedResults.get(thirdLongUrl));
  }

  @Test
  public void shouldNotReturnStatisticForAccount_exceptionIsThrown() {
    // given
    given(urlRepository.findAllByAccount_AccountId(TEST_ACCOUNT)).willThrow(RuntimeException.class);

    // when
    Throwable expectedThrowable =
        catchThrowable(() -> urlService.getStatisticsForAccount(TEST_ACCOUNT));

    // then
    assertThat(expectedThrowable).isInstanceOf(RuntimeException.class);
  }

  @Test
  public void shouldGetOriginalFromShortUrl() {
    // given
    String shortUrl = "shortUrl";
    String longUrl = "https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html";
    Url expectedUrl = new Url(shortUrl, longUrl, 302, 100L, new Account());
    given(urlRepository.findById(shortUrl)).willReturn(Optional.of(expectedUrl));

    // when
    Optional<Url> actualUrl = urlService.getUrlByShortUrl(shortUrl);

    // then
    assertThat(actualUrl.isPresent()).isTrue();
    assertThat(actualUrl.get()).isEqualTo(expectedUrl);
  }

  @Test
  public void shouldNotGetOriginalFromShortUrl() {
    // given
    String shortUrl = "shortUrl";
    given(urlRepository.findById(shortUrl)).willReturn(Optional.empty());

    // when
    Optional<Url> actualUrl = urlService.getUrlByShortUrl(shortUrl);

    // then
    assertThat(actualUrl.isPresent()).isFalse();
  }

  @Test
  public void shouldNotGetOriginalFromShortUrl_exceptionIsThrown() {
    // given
    String shortUrl = "shortUrl";
    given(urlRepository.findById(shortUrl)).willThrow(RuntimeException.class);

    // when
    Throwable expectedThrowable = catchThrowable(() -> urlService.getUrlByShortUrl(shortUrl));

    // then
    assertThat(expectedThrowable).isInstanceOf(RuntimeException.class);
  }
}
