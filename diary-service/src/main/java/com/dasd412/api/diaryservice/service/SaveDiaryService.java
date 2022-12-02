package com.dasd412.api.diaryservice.service;

import com.dasd412.api.diaryservice.controller.dto.SecurityDiaryPostRequestDTO;
import com.dasd412.api.diaryservice.domain.diary.DiabetesDiary;
import com.dasd412.api.diaryservice.domain.diary.DiaryRepository;
import com.dasd412.api.diaryservice.service.client.FindWriterFeignClient;
import com.dasd412.api.diaryservice.utils.UserContextHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
    public Long postDiaryWithEntities(SecurityDiaryPostRequestDTO dto) throws TimeoutException {
        logger.info("call writer micro service for finding writer id. correlation id :{}", UserContextHolder.getContext().getCorrelationId());
        Long writerId = findWriterFeignClient.findWriterById(dto.getWriterId());

        logger.info("saving diary... correlation id :{}", UserContextHolder.getContext().getCorrelationId());
        DiabetesDiary diary = new DiabetesDiary(writerId, dto.getFastingPlasmaGlucose(), dto.getRemark());
        diaryRepository.save(diary);

        return diary.getId();
    }

}
