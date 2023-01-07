package com.dasd412.api.diaryservice.adapter.in.web;

import brave.ScopedSpan;
import brave.Tracer;

import com.dasd412.api.diaryservice.adapter.out.web.ApiResult;
import com.dasd412.api.diaryservice.adapter.out.web.dto.delete.DiaryDeleteResponseDTO;
import com.dasd412.api.diaryservice.application.service.DeleteDiaryService;
import com.dasd412.api.diaryservice.common.utils.trace.UserContextHolder;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeoutException;

@SuppressWarnings({"unused", "static-access"})
@RestController
public class DiaryDeleteRestController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DeleteDiaryService deleteDiaryService;

    private final Tracer tracer;


    public DiaryDeleteRestController(DeleteDiaryService deleteDiaryService, Tracer tracer) {
        this.deleteDiaryService = deleteDiaryService;
        this.tracer = tracer;
    }

    @DeleteMapping("/diabetes-diary/{diaryId}")
    @RateLimiter(name = "diaryService")
    @CircuitBreaker(name = "diaryService", fallbackMethod = "fallBackDeleteDiary")
    public ApiResult<?> deleteDiary(@PathVariable Long diaryId, @RequestHeader(value = "writer-id") String writerId) throws TimeoutException {
        logger.info("correlation id in deleting diary of DiaryRestController:{}", UserContextHolder.getContext().getCorrelationId());

        ScopedSpan span = tracer.startScopedSpan("deleteDiary");

        try {
            Long removedId = deleteDiaryService.deleteDiaryWithSubEntities(diaryId, Long.parseLong(writerId));
            return ApiResult.OK(new DiaryDeleteResponseDTO(removedId));
        } catch (Exception exception) {
            exception.printStackTrace();
            return ApiResult.ERROR(exception.getMessage(), HttpStatus.BAD_REQUEST);
        } finally {
            span.tag("cud.diary.service", "delete");
            span.finish();
        }
    }

    private ApiResult<?> fallBackDeleteDiary(Long diaryId, String writerId, Throwable throwable) {
        logger.error("failed to call outer component in deleting Diary of DiaryRestController. correlation id :{} , exception : {}", UserContextHolder.getContext().getCorrelationId(), throwable.getClass());
        if (throwable.getClass().isAssignableFrom(IllegalArgumentException.class)) {
            return ApiResult.ERROR(throwable.getClass().getName(), HttpStatus.BAD_REQUEST);
        }
        return ApiResult.ERROR(throwable.getClass().getName(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
