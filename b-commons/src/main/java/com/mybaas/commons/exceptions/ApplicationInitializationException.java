package com.mybaas.commons.exceptions;

public class ApplicationInitializationException extends Throwable {
    public ApplicationInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationInitializationException(String message) {
        super(message);
    }
}
