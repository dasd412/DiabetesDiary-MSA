package com.dasd412.api.gatewayserver.filter;

import brave.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class ResponseFilter {

    private final Logger logger = LoggerFactory.getLogger(ResponseFilter.class);

    private final FilterUtils filterUtils;
    private final Tracer tracer;

    public ResponseFilter(FilterUtils filterUtils, Tracer tracer) {
        this.filterUtils = filterUtils;
        this.tracer = tracer;
    }

    @Bean
    public GlobalFilter postGlobalFilter() {
        return ((exchange, chain) -> {
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                String traceId=tracer.currentSpan().context().traceIdString();
                logger.debug("adding correlation id to the outbound headers.{}", traceId);
                exchange.getResponse().getHeaders().add(FilterUtils.CORRELATION_ID, traceId);
                logger.debug("complete outgoing request for {}.", exchange.getRequest().getURI());
            }));
        });
    }
}
