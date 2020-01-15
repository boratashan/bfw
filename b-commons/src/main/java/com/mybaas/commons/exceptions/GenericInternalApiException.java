package com.mybaas.commons.exceptions;

public class GenericInternalApiException extends RuntimeException {

    public GenericInternalApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
