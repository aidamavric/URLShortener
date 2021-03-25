package com.shortener.url.dto;

import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UrlRegistrationResponse {
  @NotBlank private String shortUrl;
}
