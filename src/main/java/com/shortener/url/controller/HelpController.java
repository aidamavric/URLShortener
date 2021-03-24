package com.shortener.url.controller;

import com.shortener.url.util.UrlShortenerConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelpController {
  @GetMapping(UrlShortenerConstants.HELP_URL)
  public String help() {
    return "help";
  }
}
