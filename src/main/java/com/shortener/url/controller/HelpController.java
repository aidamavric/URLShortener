package com.shortener.url.controller;

import com.shortener.url.util.UrlShortenerConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelpController {

  /**
   * Renders page with app instructions
   * @return help.html
   */
  @GetMapping(UrlShortenerConstants.HELP_URL)
  public String help() {
    return "help";
  }
}
