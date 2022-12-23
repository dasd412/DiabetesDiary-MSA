package com.dasd412.api.readdiaryservice.adapter.in.web;

import com.dasd412.api.readdiaryservice.adapter.out.web.ApiResult;
import com.dasd412.api.readdiaryservice.application.service.impl.ReadDiaryServiceImpl;
import com.dasd412.api.readdiaryservice.common.utils.UserContextHolder;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/diabetes-diary")
public class ReadDiaryRestController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ReadDiaryServiceImpl diaryService;

    public ReadDiaryRestController(ReadDiaryServiceImpl diaryService) {
        this.diaryService = diaryService;
    }

    @GetMapping("/{writerId}")
    @RateLimiter(name = "readDiaryService")
    @CircuitBreaker(name = "readDiaryService", fallbackMethod = "fallBackReadDiaryByWriterId")
    public ApiResult<?> readDiaryByWriterId() throws TimeoutException {
        logger.info("correlation id in reading diary by writer id of ReadDiaryRestController:{}", UserContextHolder.getContext().getCorrelationId());
        return ApiResult.OK(diaryService.findByWriterId());
    }

    @SuppressWarnings("unused")
    private ApiResult<?> fallBackReadDiaryByWriterId(Throwable throwable) {
        logger.error("failed to call outer component in reading diary by writer id of ReadDiaryRestController. correlation id :{} , exception : {}", UserContextHolder.getContext().getCorrelationId(), throwable.getClass());
        return ApiResult.ERROR(throwable.getClass().getName(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
