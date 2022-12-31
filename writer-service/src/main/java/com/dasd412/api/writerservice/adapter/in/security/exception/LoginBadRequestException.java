package com.dasd412.api.writerservice.adapter.in.security.exception;

import org.springframework.security.core.AuthenticationException;

public class LoginBadRequestException extends AuthenticationException {
    public LoginBadRequestException(String msg, Throwable t) {
        super(msg, t);
    }

    public LoginBadRequestException(String msg) {
        super(msg);
    }
}
