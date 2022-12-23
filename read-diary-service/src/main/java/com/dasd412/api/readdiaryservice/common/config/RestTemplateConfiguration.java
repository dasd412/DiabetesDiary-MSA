package com.dasd412.api.readdiaryservice.common.config;

import com.dasd412.api.readdiaryservice.common.utils.UserContextInterceptor;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
public class RestTemplateConfiguration {

    @LoadBalanced
    @Bean
    public RestTemplate getRestTemplate() {
        RestTemplate template = new RestTemplate();

        List<ClientHttpRequestInterceptor> interceptors = template.getInterceptors();

        interceptors.add(new UserContextInterceptor());
        template.setInterceptors(interceptors);
        return template;
    }
}
