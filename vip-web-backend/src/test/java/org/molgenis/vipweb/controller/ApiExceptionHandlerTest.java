package org.molgenis.vipweb.controller;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.molgenis.vipweb.UnknownEntityException;
import org.molgenis.vipweb.model.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class ApiExceptionHandlerTest {
  private ApiExceptionHandler apiExceptionHandler;

  @BeforeEach
  void setUp() {
    apiExceptionHandler = new ApiExceptionHandler();
  }

  @Test
  void handleUnknownEntityException() {
    ResponseEntity<Error> errorResponseEntity =
        apiExceptionHandler.handleUnknownEntityException(new UnknownEntityException());
    assertAll(
        () -> assertEquals(HttpStatus.BAD_REQUEST, errorResponseEntity.getStatusCode()),
        () ->
            assertEquals(
                Error.from("Requested entity is unknown or not accessible"),
                errorResponseEntity.getBody()));
  }

  @Test
  void handleRuntimeException() {
    ResponseEntity<Error> errorResponseEntity =
        apiExceptionHandler.handleRuntimeException(new RuntimeException("msg"));
    assertAll(
        () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, errorResponseEntity.getStatusCode()),
        () ->
            assertEquals(
                Error.from("An unexpected problem encountered while processing your request"),
                errorResponseEntity.getBody()));
  }
}
