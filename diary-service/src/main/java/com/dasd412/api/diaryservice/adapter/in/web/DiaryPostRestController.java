package com.dasd412.api.diaryservice.adapter.in.web;

import brave.ScopedSpan;
import brave.Tracer;
import com.dasd412.api.diaryservice.adapter.out.message.model.readdiary.dto.DiaryToReaderDTO;
import com.dasd412.api.diaryservice.adapter.out.web.ApiResult;
import com.dasd412.api.diaryservice.adapter.in.web.dto.post.DiaryPostRequestDTO;
import com.dasd412.api.diaryservice.adapter.out.web.dto.post.DiaryPostResponseDTO;
import com.dasd412.api.diaryservice.application.service.SaveDiaryService;
import com.dasd412.api.diaryservice.application.service.impl.SaveDiaryServiceImpl;
import com.dasd412.api.diaryservice.common.utils.trace.UserContextHolder;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.NoResultException;
import javax.validation.Valid;
import java.util.concurrent.TimeoutException;

@SuppressWarnings({"unused", "static-access"})
@RestController
public class DiaryPostRestController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SaveDiaryService saveDiaryService;

    private final Tracer tracer;

    public DiaryPostRestController(SaveDiaryServiceImpl saveDiaryService, Tracer tracer) {
        this.saveDiaryService = saveDiaryService;
        this.tracer = tracer;
    }

    @PostMapping("/diabetes-diary")
    @RateLimiter(name = "diaryService")
    @CircuitBreaker(name = "diaryService", fallbackMethod = "fallBackPostDiary")
    public ApiResult<?> postDiary(@RequestBody @Valid DiaryPostRequestDTO dto, @RequestHeader(value = "writer-id") String writerId) throws TimeoutException {
        logger.info("correlation id in posting diary of DiaryRestController:{}", UserContextHolder.getContext().getCorrelationId());

        ScopedSpan span = tracer.startScopedSpan("postDiary");

        try {
            DiaryToReaderDTO readerDTO = saveDiaryService.postDiaryWithEntities(Long.parseLong(writerId), dto);

            saveDiaryService.sendMessageToWriterService(readerDTO.getWriterId(), readerDTO.getDiaryId());

            saveDiaryService.sendMessageToFindDiaryService(readerDTO);

            return ApiResult.OK(new DiaryPostResponseDTO(readerDTO.getDiaryId()));
        } catch (NoResultException exception) {
            return ApiResult.ERROR("cannot find appropriate writer...", HttpStatus.BAD_REQUEST);
        } finally {
            span.tag("cud.diary.service", "create");
            span.finish();
        }
    }

    private ApiResult<?> fallBackPostDiary(DiaryPostRequestDTO dto, String writerId, Throwable throwable) {
        logger.error("failed to call outer component in posting Diary of DiaryRestController. correlation id :{} , exception : {}", UserContextHolder.getContext().getCorrelationId(), throwable.getClass());
        if (throwable.getClass().isAssignableFrom(IllegalArgumentException.class)) {
            return ApiResult.ERROR(throwable.getClass().getName(), HttpStatus.BAD_REQUEST);
        }
        return ApiResult.ERROR(throwable.getClass().getName(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
