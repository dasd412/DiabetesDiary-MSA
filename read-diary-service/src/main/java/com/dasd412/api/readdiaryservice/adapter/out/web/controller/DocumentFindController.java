package com.dasd412.api.readdiaryservice.adapter.out.web.controller;

import brave.ScopedSpan;
import brave.Tracer;
import com.dasd412.api.readdiaryservice.adapter.in.web.FoodPageVO;
import com.dasd412.api.readdiaryservice.adapter.out.web.ApiResult;
import com.dasd412.api.readdiaryservice.adapter.out.web.dto.AllFpgDTO;
import com.dasd412.api.readdiaryservice.application.service.ReadDiaryService;
import com.dasd412.api.readdiaryservice.common.utils.UserContextHolder;
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
        } catch (Exception exception) {
            exception.printStackTrace();
            return ApiResult.ERROR(exception.getMessage(), HttpStatus.BAD_REQUEST);
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
    public ApiResult<?> finalFpgBetween(@RequestHeader(value = "writer-id") String writerId, @RequestParam
    Map<String, String> timeSpan) {
        return null;
    }

    @GetMapping("/blood-sugar/all")
    public ApiResult<?> findAllBloodSugar(@RequestHeader(value = "writer-id") String writerId) {
        return null;
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
