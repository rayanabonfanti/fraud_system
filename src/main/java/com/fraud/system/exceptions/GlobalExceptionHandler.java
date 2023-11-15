package com.fraud.system.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CustomException> handleCustomException(CustomException ex) {
        int errorCode = ex.getErrorCode();
        String message = ex.getMessage();
        CustomException errorResponse = new CustomException(errorCode, message);
        log.error(ex.getMessage());
        log.error(errorResponse.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorCode));
    }

}