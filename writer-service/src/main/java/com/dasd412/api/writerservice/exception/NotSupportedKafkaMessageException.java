package com.dasd412.api.writerservice.exception;

public class NotSupportedKafkaMessageException extends IllegalArgumentException{
    public NotSupportedKafkaMessageException(String s) {
        super(s);
    }

    public NotSupportedKafkaMessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotSupportedKafkaMessageException(Throwable cause) {
        super(cause);
    }
}
