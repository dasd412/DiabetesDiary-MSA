package com.dasd412.api.diaryservice.service;

import com.dasd412.api.diaryservice.controller.dto.SecurityDiaryPostRequestDTO;
import com.dasd412.api.diaryservice.domain.diary.DiabetesDiary;
import com.dasd412.api.diaryservice.domain.diary.DiaryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.google.common.base.Preconditions.checkNotNull;

@Service
public class SaveDiaryService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DiaryRepository diaryRepository;

    public SaveDiaryService(DiaryRepository diaryRepository) {
        this.diaryRepository = diaryRepository;
    }

    //todo 스켈레톤 코드 지우고 실제 로직 넣을 필요 있음.
    @Transactional
    public Long postDiaryWithEntities(SecurityDiaryPostRequestDTO dto) {
        logger.info("post diary in service logic");

        DiabetesDiary diary = new DiabetesDiary(1L, dto.getFastingPlasmaGlucose(), dto.getRemark());
        diaryRepository.save(diary);

        return diary.getId();
    }
}
