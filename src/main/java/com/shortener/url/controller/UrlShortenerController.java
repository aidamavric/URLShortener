package com.shortener.url.controller;

import com.shortener.url.entity.Url;
import com.shortener.url.service.UrlService;
import java.util.Optional;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UrlShortenerController {

  @Value("${short.url.length}")
  private String shortUrlLength;

  private final UrlService urlService;

  @GetMapping(AntPathMatcher.DEFAULT_PATH_SEPARATOR + "*")
  public void redirect(HttpServletRequest request, HttpServletResponse httpServletResponse) {
    String shortUrl = request.getRequestURI().substring(1);
    log.info("Received request for redirection of short url {}", request.getRequestURI());
    String alphaNumericPattern = "^[a-zA-Z0-9]{" + Integer.parseInt(shortUrlLength) + "}$";
    if (!Pattern.matches(alphaNumericPattern, shortUrl)) {
      log.error("Unable to redirect to non existing url {}", request.getRequestURI());
      httpServletResponse.setStatus(HttpStatus.NOT_FOUND.value());
      return;
    }

    Optional<Url> originalFromShortUrl = urlService.getUrlByShortUrl(shortUrl);
    if (!originalFromShortUrl.isPresent()) {
      log.error("Unable to redirect to non existing url {}", request.getRequestURI());
      httpServletResponse.setStatus(HttpStatus.NOT_FOUND.value());
      return;
    }

    Url url = originalFromShortUrl.get();
    urlService.updateStatistic(url);
    httpServletResponse.setHeader(HttpHeaders.LOCATION, url.getOriginalUrl());
    httpServletResponse.setStatus(url.getRedirectType());
    log.info(
        "Redirecting short url {} to original url {}",
        request.getRequestURI(),
        url.getOriginalUrl());
  }
}
