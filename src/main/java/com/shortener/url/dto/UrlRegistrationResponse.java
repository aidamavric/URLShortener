package com.shortener.url.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UrlRegistrationResponse {
  private String shortUrl;
}
