package com.dasd412.api.diaryservice.adapter.in.web;

import brave.ScopedSpan;
import brave.Tracer;

import com.dasd412.api.diaryservice.adapter.in.web.dto.delete.DiaryDeleteRequestDTO;
import com.dasd412.api.diaryservice.adapter.out.web.ApiResult;
import com.dasd412.api.diaryservice.adapter.out.web.dto.delete.DiaryDeleteResponseDTO;
import com.dasd412.api.diaryservice.application.service.DeleteDiaryService;
import com.dasd412.api.diaryservice.common.utils.trace.UserContextHolder;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.NoResultException;
import javax.validation.Valid;
import java.util.concurrent.TimeoutException;

@RestController
public class DiaryDeleteRequestController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DeleteDiaryService deleteDiaryService;

    private final Tracer tracer;

    public DiaryDeleteRequestController(DeleteDiaryService deleteDiaryService, Tracer tracer) {
        this.deleteDiaryService = deleteDiaryService;
        this.tracer = tracer;
    }

    @DeleteMapping("/diabetes-diary")
    @RateLimiter(name = "diaryService")
    @CircuitBreaker(name = "diaryService", fallbackMethod = "fallBackDeleteDiary")
    public ApiResult<?> deleteDiary(@RequestBody @Valid DiaryDeleteRequestDTO dto) throws TimeoutException {
        logger.info("correlation id in deleting diary of DiaryRestController:{}", UserContextHolder.getContext().getCorrelationId());

        ScopedSpan span = tracer.startScopedSpan("updateDiary");

        try {
            Long removedId = deleteDiaryService.deleteDiaryWithSubEntities(dto);
            return ApiResult.OK(new DiaryDeleteResponseDTO(removedId));
        } catch (Exception exception) {
            exception.printStackTrace();
            return ApiResult.ERROR(exception.getMessage(), HttpStatus.BAD_REQUEST);
        } finally {
            span.tag("cud.diary.service", "delete");
            span.finish();
        }
    }

    @SuppressWarnings("unused")
    private ApiResult<?> fallBackDeleteDiary(DiaryDeleteRequestDTO dto, Throwable throwable) {
        logger.error("failed to call outer component in deleting Diary of DiaryRestController. correlation id :{} , exception : {}", UserContextHolder.getContext().getCorrelationId(), throwable.getClass());
        if (throwable.getClass().isAssignableFrom(IllegalArgumentException.class)) {
            return ApiResult.ERROR(throwable.getClass().getName(), HttpStatus.BAD_REQUEST);
        }
        return ApiResult.ERROR(throwable.getClass().getName(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
