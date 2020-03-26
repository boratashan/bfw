package com.mybaas.commons.mysql;

public class MySqlClientConfigurationException extends Exception {
    public MySqlClientConfigurationException(String message) {
        super(message);
    }

    public MySqlClientConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
