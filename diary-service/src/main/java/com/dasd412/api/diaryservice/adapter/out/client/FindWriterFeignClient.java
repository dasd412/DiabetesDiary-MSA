package com.dasd412.api.diaryservice.adapter.out.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.concurrent.TimeoutException;

@FeignClient("writer-service")
public interface FindWriterFeignClient {

    @CircuitBreaker(name="writerService")
    @RequestMapping(method= RequestMethod.GET,value="/writer/{writerId}",consumes = "application/json")
    Long findWriterById(@PathVariable("writerId")Long writerId) throws TimeoutException;

}
