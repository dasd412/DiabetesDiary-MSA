package com.dasd412.api.writerservice.adapter.out.web.exception;

public class EmailExistException extends RuntimeException{

    public EmailExistException(String message) {
        super(message);
    }

    public EmailExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
