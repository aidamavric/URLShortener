package com.shortener.url.service;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

import com.shortener.url.dto.UrlRegistrationRequest;
import com.shortener.url.entity.Account;
import com.shortener.url.entity.Url;
import com.shortener.url.repository.UrlRepository;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UrlService {

  private String shortUrlLength;
  private final UrlRepository urlRepository;

  @Value("${short.url.length}")
  public void setShortUrlLength(String shortUrlLength) {
    this.shortUrlLength = shortUrlLength;
  }

  /**
   * Generates and saves short url for a given request
   *
   * @param urlRegistrationRequest request with long url and redirection type
   * @return generated alphanumeric short url
   */
  @Transactional
  public String generateAndSaveUrl(UrlRegistrationRequest urlRegistrationRequest) {
    String shortUrl;
    do {
      shortUrl =
          RandomStringUtils.random(
              Integer.parseInt(shortUrlLength), 0, 0, true, true, null, new SecureRandom());
    } while (urlRepository.existsById(shortUrl));

    UserDetails userDetails = getAuthenticationDetails();
    Url url =
        new Url(
            shortUrl,
            urlRegistrationRequest.getUrl(),
            urlRegistrationRequest.getRedirectType(),
            0L,
            new Account(userDetails.getUsername()));
    urlRepository.save(url);
    return shortUrl;
  }

  /**
   * Fetches visit statistic of links for a given account
   *
   * @param accountId id of Account
   * @return map with links and their corresponding number of visits
   */
  @Transactional(readOnly = true)
  public Map<String, Long> getStatisticsForAccount(String accountId) {
    List<Url> urls = urlRepository.findAllByAccount_AccountId(accountId);
    // summarizing statistics for links -> there could be duplicates
    return urls.stream()
        .collect(groupingBy(Url::getOriginalUrl))
        .entrySet()
        .stream()
        .collect(
            toMap(
                Entry::getKey,
                entry ->
                    entry.getValue().stream()
                        .reduce(
                            0L,
                            (partialStatisticResult, url) ->
                                partialStatisticResult + url.getStatistic(),
                            Long::sum)));
  }

  /**
   * Fetches registered Url for a given short url
   *
   * @param shortUrl short url
   * @return Url object containing long url, redirectionType and statistic
   */
  @Transactional(readOnly = true)
  public Optional<Url> getUrlByShortUrl(String shortUrl) {
    return urlRepository.findById(shortUrl);
  }

  /**
   * Updates visit statistic for a given url
   *
   * @param url Url object to update statistic
   */
  @Transactional
  public void updateStatistic(Url url) {
    url.setStatistic(url.getStatistic() + 1);
    urlRepository.save(url);
  }

  /**
   * Fetches user details of current authenticated user
   *
   * @return UserDetails object with accountId and password
   */
  UserDetails getAuthenticationDetails() {
    return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }
}
