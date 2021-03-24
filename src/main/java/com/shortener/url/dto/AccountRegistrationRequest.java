package com.shortener.url.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.shortener.url.util.UrlShortenerConstants;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = false)
public class AccountRegistrationRequest {

  @NotBlank(message = UrlShortenerConstants.ACCOUNT_ID_MUST_NOT_BE_EMPTY_OR_NULL)
  @JsonProperty(value = "AccountId")
  String accountId;
}
