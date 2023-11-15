package com.fraud.system.exceptions;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final int errorCode;
    private final String message;

    public CustomException(Integer status, String message) {
        super(message);
        this.errorCode = status;
        this.message = message;
    }
}
