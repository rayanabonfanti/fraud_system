package com.fraud.system.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GlobalExpcetionHandlerTest {
    @Test
    void testHandlerCustomException() {
        CustomException mockException = new CustomException(404, "Not Found");
        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();
        ResponseEntity<CustomException> exceptionResponseEntity = new ResponseEntity<>(
                new CustomException(404, "Not Found"),
                HttpStatus.NOT_FOUND
        );
        ResponseEntity<CustomException> response = exceptionHandler.handleCustomException(mockException);
        assertEquals(exceptionResponseEntity.getStatusCode(), response.getStatusCode());
        assertEquals(exceptionResponseEntity.getBody().getMessage(), response.getBody().getMessage());
    }
}
