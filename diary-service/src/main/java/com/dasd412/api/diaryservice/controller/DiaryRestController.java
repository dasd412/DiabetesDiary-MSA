package com.dasd412.api.diaryservice.controller;

import brave.ScopedSpan;
import brave.Tracer;
import com.dasd412.api.diaryservice.controller.dto.DiaryPostRequestDTO;
import com.dasd412.api.diaryservice.controller.dto.DiaryPostResponseDTO;
import com.dasd412.api.diaryservice.service.SaveDiaryService;
import com.dasd412.api.diaryservice.utils.trace.UserContextHolder;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.NoResultException;
import javax.validation.Valid;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/diabetes-diary")
public class DiaryRestController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SaveDiaryService saveDiaryService;

    private final Tracer tracer;

    public DiaryRestController(SaveDiaryService saveDiaryService, Tracer tracer) {
        this.saveDiaryService = saveDiaryService;
        this.tracer = tracer;
    }

    @PostMapping
    @RateLimiter(name = "diaryService")
    @CircuitBreaker(name = "diaryService", fallbackMethod = "fallBackPostDiary")
    public ApiResult<?> postDiary(@RequestBody @Valid DiaryPostRequestDTO dto) throws TimeoutException {
        logger.info("correlation id in posting diary of DiaryRestController:{}", UserContextHolder.getContext().getCorrelationId());

        ScopedSpan span = tracer.startScopedSpan("postDiary");

        try {
            Long diaryId = saveDiaryService.postDiaryWithEntities(dto);
            return ApiResult.OK(new DiaryPostResponseDTO(diaryId));
        } catch (NoResultException exception) {
            return ApiResult.ERROR("cannot find appropriate writer...", HttpStatus.BAD_REQUEST);
        } finally {
            span.tag("cud.diary.service", "create");
            span.finish();
        }
    }

    @SuppressWarnings("unused")
    private ApiResult<?> fallBackPostDiary(DiaryPostRequestDTO dto, Throwable throwable) {
        logger.error("failed to call outer component in posting Diary of DiaryRestController. correlation id :{} , exception : {}", UserContextHolder.getContext().getCorrelationId(), throwable.getClass());
        if (throwable.getClass().isAssignableFrom(IllegalArgumentException.class)) {
            return ApiResult.ERROR(throwable.getClass().getName(), HttpStatus.BAD_REQUEST);
        }
        return ApiResult.ERROR(throwable.getClass().getName(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
