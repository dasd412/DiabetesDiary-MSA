package com.dasd412.api.diaryservice.adapter.in.web;

import brave.ScopedSpan;
import brave.Tracer;
import com.dasd412.api.diaryservice.adapter.in.web.dto.update.DiaryUpdateRequestDTO;
import com.dasd412.api.diaryservice.adapter.out.message.model.readdiary.dto.DiaryToReaderDTO;
import com.dasd412.api.diaryservice.adapter.out.web.ApiResult;
import com.dasd412.api.diaryservice.adapter.out.web.dto.update.DiaryUpdateResponseDTO;
import com.dasd412.api.diaryservice.application.service.UpdateDiaryService;
import com.dasd412.api.diaryservice.common.utils.trace.UserContextHolder;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.NoResultException;
import javax.validation.Valid;
import java.util.concurrent.TimeoutException;

@SuppressWarnings({"unused", "static-access"})
@RestController
public class DiaryUpdateRestController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UpdateDiaryService updateDiaryService;

    private final Tracer tracer;

    public DiaryUpdateRestController(UpdateDiaryService updateDiaryService, Tracer tracer) {
        this.updateDiaryService = updateDiaryService;
        this.tracer = tracer;
    }

    @PutMapping("/diabetes-diary")
    @RateLimiter(name = "diaryService")
    @CircuitBreaker(name = "diaryService", fallbackMethod = "fallBackUpdateDiary")
    public ApiResult<?> updateDiary(@RequestBody @Valid DiaryUpdateRequestDTO dto, @RequestHeader(value = "writer-id") String writerId) throws TimeoutException {
        logger.info("correlation id in updating diary of DiaryRestController:{}", UserContextHolder.getContext().getCorrelationId());

        ScopedSpan span = tracer.startScopedSpan("updateDiary");

        try {
            DiaryToReaderDTO readerDTO = updateDiaryService.updateDiaryWithEntities(Long.parseLong(writerId), dto);

            updateDiaryService.sendMessageToFindDiaryService(readerDTO);

            return ApiResult.OK(new DiaryUpdateResponseDTO(readerDTO.getDiaryId()));
        } catch (NoResultException exception) {
            return ApiResult.ERROR(exception.getMessage(), HttpStatus.BAD_REQUEST);
        } finally {
            span.tag("cud.diary.service", "update");
            span.finish();
        }
    }

    private ApiResult<?> fallBackUpdateDiary(DiaryUpdateRequestDTO dto, String writerId, Throwable throwable) {
        logger.error("failed to call outer component in updating Diary of DiaryRestController. correlation id :{} , exception : {}", UserContextHolder.getContext().getCorrelationId(), throwable.getClass());
        if (throwable.getClass().isAssignableFrom(IllegalArgumentException.class)) {
            return ApiResult.ERROR(throwable.getClass().getName(), HttpStatus.BAD_REQUEST);
        }
        return ApiResult.ERROR(throwable.getClass().getName(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
