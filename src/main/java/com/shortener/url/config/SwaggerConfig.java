package com.shortener.url.config;

import com.shortener.url.util.UrlShortenerConstants;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "URLShortener API", version = "v1"))
@SecurityScheme(
    name = UrlShortenerConstants.OPEN_API_BASIC_AUTH,
    type = SecuritySchemeType.HTTP,
    scheme = "basic")
public class SwaggerConfig {}
