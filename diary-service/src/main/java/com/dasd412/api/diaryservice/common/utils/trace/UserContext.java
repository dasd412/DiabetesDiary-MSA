package com.dasd412.api.diaryservice.common.utils.trace;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class UserContext {

    public static final String CORRELATION_ID = "diary-correlation-id";
    public static final String AUTH_TOKEN = "Authorization";
    public static final String USER_ID = "writer-id";
    public static final String DIARY_ID = "diary-id";

    private static final ThreadLocal<String> correlationId = new ThreadLocal<>();
    private static final ThreadLocal<String> authToken = new ThreadLocal<>();
    private static final ThreadLocal<String> userId = new ThreadLocal<>();
    private static final ThreadLocal<String> diaryId = new ThreadLocal<>();

    public static String getCorrelationId() {
        return correlationId.get();
    }

    public static String getAuthToken() {
        return authToken.get();
    }

    public static String getUserId() {
        return userId.get();
    }

    public static String getDiaryId() {
        return diaryId.get();
    }

    public void setCorrelationId(String paramCorrelationId) {
        correlationId.set(paramCorrelationId);
    }

    public void setAuthToken(String paramAuthToken) {
        authToken.set(paramAuthToken);
    }

    public void setUserId(String paramUserId) {
        userId.set(paramUserId);
    }

    public void setDiaryId(String paramDiaryId) {
        diaryId.set(paramDiaryId);
    }

    public static HttpHeaders getHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(CORRELATION_ID, getCorrelationId());
        httpHeaders.set(AUTH_TOKEN, getAuthToken());
        return httpHeaders;
    }
}
