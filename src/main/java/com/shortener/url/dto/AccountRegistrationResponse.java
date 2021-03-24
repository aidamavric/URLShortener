package com.shortener.url.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountRegistrationResponse {

  private Boolean success;
  private String description;

  @JsonInclude(Include.NON_NULL)
  private String password;
}
