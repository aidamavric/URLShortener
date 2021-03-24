package com.shortener.url.dto;

import com.shortener.url.util.UrlShortenerConstants;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlRegistrationRequest {

  @URL(message = UrlShortenerConstants.URL_MUST_BE_VALID_URL_SYNTAX)
  @NotBlank(message = UrlShortenerConstants.URL_MUST_NOT_BE_EMPTY_OR_NULL)
  private String url;

  @Min(value = 301, message = UrlShortenerConstants.SHOULD_HAVE_ON_OF_FOLLOWING_VALUES_301_OR_302)
  @Max(value = 302, message = UrlShortenerConstants.SHOULD_HAVE_ON_OF_FOLLOWING_VALUES_301_OR_302)
  private int redirectType = 302;
}
