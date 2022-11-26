package com.dasd412.api.diaryservice.service;

import com.dasd412.api.diaryservice.controller.dto.SecurityDiaryPostRequestDTO;
import com.dasd412.api.diaryservice.domain.EntityId;
import com.dasd412.api.diaryservice.domain.diary.DiabetesDiary;
import com.dasd412.api.diaryservice.domain.diary.DiaryRepository;
import com.dasd412.api.diaryservice.service.client.FindWriterFeignClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.google.common.base.Preconditions.checkNotNull;

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
    @Transactional
    public Long postDiaryWithEntities(SecurityDiaryPostRequestDTO dto) {
        logger.info("post diary in service logic");

        Long writerId=findWriterFeignClient.findWriterById(dto.getWriterId());


        DiabetesDiary diary = new DiabetesDiary(writerId, dto.getFastingPlasmaGlucose(), dto.getRemark());
        diaryRepository.save(diary);

        return diary.getId();
    }
}
