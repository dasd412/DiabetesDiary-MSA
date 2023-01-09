package com.dasd412.api.diaryservice.adapter.in.message.exception;

public class NotSupportedActionEnumException extends RuntimeException{

    public NotSupportedActionEnumException(String message) {
        super(message);
    }

    public NotSupportedActionEnumException(String message, Throwable cause) {
        super(message, cause);
    }
}
