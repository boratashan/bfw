package com.mybaas.commons.model;

public class QueryStringParsingException extends RuntimeException {
    public QueryStringParsingException(String message) {
        super(message);
    }

    public QueryStringParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
