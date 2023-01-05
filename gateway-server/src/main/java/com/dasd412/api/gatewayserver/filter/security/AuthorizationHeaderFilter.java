package com.dasd412.api.gatewayserver.filter.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dasd412.api.gatewayserver.security.JWTTokenProvider;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;;

@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    private final JWTTokenProvider jwtTokenProvider;

    private final Logger logger = LoggerFactory.getLogger(AuthorizationHeaderFilter.class);

    public AuthorizationHeaderFilter(JWTTokenProvider jwtTokenProvider) {
        super(Config.class);
        this.jwtTokenProvider = jwtTokenProvider;
    }

    static class Config {

    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            logger.info("Staring jwt authorization... request is {}" ,exchange.getRequest());

            HttpHeaders headers = request.getHeaders();

            //프론트에서 요청을 쏠 때, Authorization header에 토큰이 담겨 있어야 한다.
            if (!headers.containsKey(HttpHeaders.AUTHORIZATION)) {

            }

            String authorizationHeader = headers.get(HttpHeaders.AUTHORIZATION).get(0);

            String token = authorizationHeader.replace("Bearer", "");

            jwtTokenProvider.validateJwtToken(token);

            String subject = jwtTokenProvider.retrieveSubject(token);
            String writerId = jwtTokenProvider.retrieveWriterId(token);

            ServerHttpRequest requestWithHeader = request.mutate()
                    .header("user-name", subject)
                    .header("writer-id", writerId)
                    .build();

            logger.info("jwt authorization end ...");

            return chain.filter(exchange.mutate().request(requestWithHeader).build());
        });
    }

}
