package com.dasd412.api.readdiaryservice.adapter.in.msessage;

public enum WriterActionEnum {
    DELETED,
    NOT_SUPPORTED;

    public static Enum compare(String string){
        try {
            return valueOf(string.toUpperCase());
        } catch (IllegalArgumentException e) {
            return NOT_SUPPORTED;
        }
    }
}
