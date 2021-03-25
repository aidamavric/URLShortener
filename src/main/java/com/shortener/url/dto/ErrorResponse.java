package com.shortener.url.dto;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {

  @NotNull private int status;
  @NotNull private String error;
  @NotEmpty private List<String> message;
}
