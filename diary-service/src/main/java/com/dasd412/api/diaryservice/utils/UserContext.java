package com.dasd412.api.diaryservice.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class UserContext {

    public static final String CORRELATION_ID = "diary-correlation-id";
    public static final String AUTH_TOKEN = "Authorization";
    public static final String USER_ID = "writer-id";
    public static final String DIARY_ID = "diary-id";

    private String correlationId = "";
    private String authToken = "";
    private String userId = "";
    private String diaryId = "";

}
