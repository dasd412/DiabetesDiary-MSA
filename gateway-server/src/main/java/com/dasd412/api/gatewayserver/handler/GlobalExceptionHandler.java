package com.dasd412.api.gatewayserver.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    @Autowired
    private ObjectMapper objectMapper;

    private final Logger logger= LoggerFactory.getLogger(GlobalExceptionHandler.class);

    //response body에 에러 내용을 작성해줌.
    @Override
    public Mono<Void> handle(ServerWebExchange serverWebExchange, Throwable throwable) {

        logger.debug("throwable is : {}",throwable.getMessage());

        List<Class<? extends RuntimeException>> jwtExceptions =
                List.of(SignatureException.class,
                        MalformedJwtException.class,
                        UnsupportedJwtException.class,
                        IllegalArgumentException.class);

        Class<? extends Throwable> exceptionClass = throwable.getClass();

        Map<String, Object> responseBody = new HashMap<>();

        if (exceptionClass == ExpiredJwtException.class) {
            serverWebExchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            serverWebExchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

            responseBody.put("message", "access token has expired");

        } else if (jwtExceptions.contains(exceptionClass)) {
            serverWebExchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            serverWebExchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

            responseBody.put("message", "invalid access token");
        } else {
            serverWebExchange.getResponse().setStatusCode(serverWebExchange.getResponse().getStatusCode());
            serverWebExchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

            responseBody.put("message", throwable.getMessage());
        }

        DataBuffer wrap = null;
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(responseBody);
            wrap = serverWebExchange.getResponse().bufferFactory().wrap(bytes);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return serverWebExchange.getResponse().writeWith(Flux.just(wrap));
    }
}
