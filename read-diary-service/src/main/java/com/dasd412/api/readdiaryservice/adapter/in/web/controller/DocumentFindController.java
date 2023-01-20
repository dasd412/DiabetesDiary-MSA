package com.dasd412.api.readdiaryservice.adapter.in.web.controller;

import brave.ScopedSpan;
import brave.Tracer;
import com.dasd412.api.readdiaryservice.adapter.in.web.FoodPageVO;
import com.dasd412.api.readdiaryservice.adapter.out.web.ApiResult;
import com.dasd412.api.readdiaryservice.adapter.out.web.dto.AllBloodSugarDTO;
import com.dasd412.api.readdiaryservice.adapter.out.web.dto.AllFpgDTO;
import com.dasd412.api.readdiaryservice.adapter.out.web.dto.FpgBetweenDTO;
import com.dasd412.api.readdiaryservice.application.service.ReadDiaryService;
import com.dasd412.api.readdiaryservice.common.utils.trace.UserContextHolder;
import com.dasd412.api.readdiaryservice.domain.diary.DiabetesDiaryDocument;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@RestController
public class DocumentFindController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ReadDiaryService readDiaryService;

    private final Tracer tracer;

    public DocumentFindController(ReadDiaryService readDiaryService, Tracer tracer) {
        this.readDiaryService = readDiaryService;
        this.tracer = tracer;
    }

    @GetMapping("/fpg/all")
    @RateLimiter(name = "readDiaryService")
    @CircuitBreaker(name = "readDiaryService", fallbackMethod = "fallbackFindAllFpg")
    public ApiResult<?> findAllFpg(@RequestHeader(value = "writer-id") String writerId) throws TimeoutException {
        logger.info("find all fpg in document find controller : {} ", UserContextHolder.getContext().getCorrelationId());

        ScopedSpan span = tracer.startScopedSpan("findAllFpg");
        try {
            List<DiabetesDiaryDocument> diaryDocumentList = readDiaryService.getDiabetesDiariesOfWriter(writerId);

            List<AllFpgDTO> dtoList = diaryDocumentList.stream().map(AllFpgDTO::new)
                    .sorted(Comparator.comparing(AllFpgDTO::getTimeStamp))
                    .collect(Collectors.toList());

            return ApiResult.OK(dtoList);
        } finally {
            span.tag("read.diary.service", "allFpg");
            span.finish();
        }
    }

    private ApiResult<?> fallbackFindAllFpg(String writerId, Throwable throwable) {
        logger.error("failed to call outer component in finding all fpg in DocumentFindController. correlation id :{} , exception : {}", UserContextHolder.getContext().getCorrelationId(), throwable.getClass());
        if (throwable.getClass().isAssignableFrom(IllegalArgumentException.class)) {
            return ApiResult.ERROR(throwable.getClass().getName(), HttpStatus.BAD_REQUEST);
        }
        return ApiResult.ERROR(throwable.getClass().getName(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/fpg/between")
    @RateLimiter(name = "readDiaryService")
    @CircuitBreaker(name = "readDiaryService", fallbackMethod = "fallBackFindFpgBetween")
    public ApiResult<?> findFpgBetween(@RequestHeader(value = "writer-id") String writerId, @RequestParam
    Map<String, String> timeSpan) throws TimeoutException {
        logger.info("find fpg between time span in document find controller : {} ", UserContextHolder.getContext().getCorrelationId());

        ScopedSpan span = tracer.startScopedSpan("findFpgBetween");

        try {
            List<DiabetesDiaryDocument> diaryDocumentList = readDiaryService.getDiariesBetweenTimeSpan(writerId, timeSpan);

            List<FpgBetweenDTO> dtoList = diaryDocumentList.stream().map(FpgBetweenDTO::new)
                    .sorted(Comparator.comparing(FpgBetweenDTO::getTimeStamp))
                    .collect(Collectors.toList());

            return ApiResult.OK(dtoList);
        } finally {
            span.tag("read.diary.service", "fpgBetween");
            span.finish();
        }
    }

    private ApiResult<?> fallBackFindFpgBetween(String writerId, Map<String, String> timeSpan, Throwable throwable) {
        logger.error("failed to call outer component in finding fpg  between time span in DocumentFindController. correlation id :{} , exception : {}", UserContextHolder.getContext().getCorrelationId(), throwable.getClass());
        if (throwable.getClass().isAssignableFrom(IllegalArgumentException.class)) {
            return ApiResult.ERROR(throwable.getClass().getName(), HttpStatus.BAD_REQUEST);
        }
        return ApiResult.ERROR(throwable.getClass().getName(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/blood-sugar/all")
    @RateLimiter(name = "readDiaryService")
    @CircuitBreaker(name = "readDiaryService", fallbackMethod = "fallBackFindAllBloodSugar")
    public ApiResult<?> findAllBloodSugar(@RequestHeader(value = "writer-id") String writerId) throws TimeoutException {
        logger.info("find all blood sugar in document find controller : {} ", UserContextHolder.getContext().getCorrelationId());

        ScopedSpan span = tracer.startScopedSpan("findAllBloodSugar");

        try {
            List<AllBloodSugarDTO> dtoList = readDiaryService.getAllBloodSugarOfWriter(writerId);

            return ApiResult.OK(dtoList);
        } finally {
            span.tag("read.diary.service", "allBloodSugar");
            span.finish();
        }
    }

    private ApiResult<?> fallBackFindAllBloodSugar(String writerId, Throwable throwable) {
        logger.error("failed to call outer component in finding all blood sugar in DocumentFindController. correlation id :{} , exception : {}", UserContextHolder.getContext().getCorrelationId(), throwable.getClass());
        if (throwable.getClass().isAssignableFrom(IllegalArgumentException.class)) {
            return ApiResult.ERROR(throwable.getClass().getName(), HttpStatus.BAD_REQUEST);
        }
        return ApiResult.ERROR(throwable.getClass().getName(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/blood-sugar/between")
    public ApiResult<?> findBloodSugarBetween(@RequestHeader(value = "writer-id") String writerId, @RequestParam
    Map<String, String> timeSpan) {
        return null;
    }

    @GetMapping("/food/list")
    public ApiResult<?> findFoodList(@RequestHeader(value = "writer-id") String writerId, FoodPageVO foodPageVO) {
        return null;
    }
}
