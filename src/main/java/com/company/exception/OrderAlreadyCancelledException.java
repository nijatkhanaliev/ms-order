package com.company.exception;

import lombok.Getter;

@Getter
public class OrderAlreadyCancelledException extends RuntimeException {

    private final String errorMessage;
    private final String errorCode;

    public OrderAlreadyCancelledException(String errorMessage, String errorCode) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
