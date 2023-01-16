
package com.dasd412.api.diaryservice.common.utils.trace;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;

import java.io.IOException;

@SuppressWarnings({"static-access"})
public class UserContextInterceptor implements ClientHttpRequestInterceptor {

    @Override
    @NonNull
    public ClientHttpResponse intercept(HttpRequest httpRequest, @NonNull byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {

        HttpHeaders httpHeaders = httpRequest.getHeaders();
        httpHeaders.add(UserContext.CORRELATION_ID, UserContextHolder.getContext().getCorrelationId());
        httpHeaders.add(UserContext.AUTH_TOKEN, UserContextHolder.getContext().getAuthToken());

        return clientHttpRequestExecution.execute(httpRequest, bytes);
    }
}
