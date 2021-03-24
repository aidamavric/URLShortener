package com.shortener.url.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {

  private int status;
  private String error;
  private List<String> message;
}
