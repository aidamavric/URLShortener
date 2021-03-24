package com.shortener.url.controller.error;

import com.shortener.url.dto.ErrorResponse;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
@RestController
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  /**
   * Method overridden to implement custom logic for handling exception when arguments annotated
   * with @Valid failed validation
   *
   * @param ex MethodArgumentNotValidException thrown when argument validation fails
   * @param headers http headers
   * @param status http status for such a case
   * @param request web request
   * @return custom response entity with default validation messages
   */
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    List<String> errorMessages =
        ex.getBindingResult().getFieldErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .collect(Collectors.toList());

    log.error("Argument validation exception occurred: {}", ex.getMessage(), ex);

    return new ResponseEntity<>(
        ErrorResponse.builder()
            .status(status.value())
            .message(errorMessages)
            .error(status.getReasonPhrase())
            .build(),
        status);
  }

  /**
   * Error handling for all other exceptions that don't have specific handlers
   *
   * @param ex exception that is thrown
   * @return custom response entity with exception message
   */
  @ExceptionHandler({Exception.class})
  public ResponseEntity<Object> handleAll(Exception ex) {
    log.error("Unexpected exception occurred: {}.", ex.getMessage(), ex);
    return new ResponseEntity<>(
        ErrorResponse.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("Internal Server Error")
            .message(Collections.singletonList(ex.getLocalizedMessage()))
            .build(),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
