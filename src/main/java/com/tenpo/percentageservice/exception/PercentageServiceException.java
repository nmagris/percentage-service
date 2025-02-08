package com.tenpo.percentageservice.exception;

import org.springframework.http.HttpStatus;

public class PercentageServiceException extends RuntimeException {

    private final HttpStatus status;
    private final String message;

    public PercentageServiceException(final HttpStatus status, final String message) {
        super(message);
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
