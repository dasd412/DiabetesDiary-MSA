package com.dasd412.api.diaryservice.service;

import com.dasd412.api.diaryservice.controller.dto.SecurityDiaryPostRequestDTO;
import com.dasd412.api.diaryservice.domain.diary.DiabetesDiary;
import com.dasd412.api.diaryservice.domain.diary.DiaryRepository;
import com.dasd412.api.diaryservice.service.client.FindWriterFeignClient;
import com.dasd412.api.diaryservice.utils.UserContextHolder;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeoutException;

@Service
public class SaveDiaryService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DiaryRepository diaryRepository;

    private final FindWriterFeignClient findWriterFeignClient;

    public SaveDiaryService(DiaryRepository diaryRepository, FindWriterFeignClient findWriterFeignClient) {
        this.diaryRepository = diaryRepository;
        this.findWriterFeignClient = findWriterFeignClient;
    }

    //todo 스켈레톤 코드 지우고 실제 로직 넣을 필요 있음.
    @RateLimiter(name = "diaryService")
    public Long postDiaryWithEntities(SecurityDiaryPostRequestDTO dto) throws TimeoutException {
        logger.info("call writer micro service for finding writer id. correlation id :{}", UserContextHolder.getContext().getCorrelationId());

        Long writerId = findWriterFeignClient.findWriterById(dto.getWriterId());

        return makeDiary(writerId, dto).orElseThrow(TimeoutException::new);
    }

    @CircuitBreaker(name = "diaryService", fallbackMethod = "fallBackMakeDiary")
    private Optional<Long> makeDiary(Long writerId, SecurityDiaryPostRequestDTO dto) throws TimeoutException {
        logger.info("saving diary... correlation id :{}", UserContextHolder.getContext().getCorrelationId());

        DiabetesDiary diary = new DiabetesDiary(writerId, dto.getFastingPlasmaGlucose(), dto.getRemark());
        diaryRepository.save(diary);

        return Optional.of(diary.getId());
    }

    @SuppressWarnings("unused")
    private Optional<Long> fallBackMakeDiary(Long writerId, SecurityDiaryPostRequestDTO dto, Throwable throwable) {
        logger.error("failed to call DB in makeDiary. correlation id :{} , exception : {}", UserContextHolder.getContext().getCorrelationId(), throwable.getMessage());
        return Optional.empty();
    }
}
