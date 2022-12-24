package com.dasd412.api.gatewayserver.filter;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;
import java.util.Optional;

@Component
public class FilterUtils {

    public static final String CORRELATION_ID = "diary-correlation-id";
    public static final String AUTH_TOKEN = "Authorization";
    public static final String USER_ID = "writer-id";
    public static final String DIARY_ID = "diary-id";

    public Optional<String> getCorrelationId(HttpHeaders httpHeaders) {
        List<String> header = httpHeaders.get(CORRELATION_ID);

        if (header != null) {
            return header.stream().findFirst();
        } else {
            return Optional.empty();
        }
    }

    public ServerWebExchange setRequestHeader(ServerWebExchange exchange, String key, String value) {
        return exchange.mutate().request(exchange.getRequest().mutate().header(key, value).build()).build();
    }

    public ServerWebExchange setCorrelationId(ServerWebExchange exchange, String correlationId) {
        return this.setRequestHeader(exchange, CORRELATION_ID, correlationId);
    }
}
