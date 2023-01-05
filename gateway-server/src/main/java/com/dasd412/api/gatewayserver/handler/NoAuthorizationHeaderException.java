package com.dasd412.api.gatewayserver.handler;

public class NoAuthorizationHeaderException extends RuntimeException{

    public NoAuthorizationHeaderException(String message) {
        super(message);
    }

    public NoAuthorizationHeaderException(String message, Throwable cause) {
        super(message, cause);
    }
}
