package org.molgenis.vipweb.controller;

import org.molgenis.vipweb.UnknownEntityException;
import org.molgenis.vipweb.model.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice(basePackageClasses = ApiController.class)
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = UnknownEntityException.class)
    public ResponseEntity<Error> handleUnknownEntityException(
            UnknownEntityException unknownEntityException) {
        logger.error("error processing request", unknownEntityException);
        return new ResponseEntity<>(
                Error.from("Requested entity is unknown or not accessible"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<Error> handleRuntimeException(RuntimeException runtimeException) {
        logger.error("error processing request", runtimeException);
        return new ResponseEntity<>(
                Error.from("An unexpected problem encountered while processing your request"),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
