package com.dasd412.api.writerservice.adapter.out.web.exception;

public class UserNameExistException extends RuntimeException{

    public UserNameExistException(String message) {
        super(message);
    }

    public UserNameExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
