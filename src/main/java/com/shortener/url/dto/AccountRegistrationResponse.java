package com.shortener.url.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountRegistrationResponse {

  @NotNull private Boolean success;
  @NotBlank private String description;

  @JsonInclude(Include.NON_NULL)
  private String password;
}
