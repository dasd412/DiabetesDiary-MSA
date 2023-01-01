package com.dasd412.api.writerservice.adapter.in.message;

public enum DiaryActionEnum {
    CREATED,
    DELETED,
    NOT_SUPPORTED;

    public static Enum compare(String string) {
        try {
            return valueOf(string.toUpperCase());
        } catch (IllegalArgumentException e) {
            return NOT_SUPPORTED;
        }
    }
}
