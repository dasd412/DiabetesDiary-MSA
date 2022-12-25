package com.dasd412.api.diaryservice.application.service.impl;

import com.dasd412.api.diaryservice.adapter.out.message.source.KafkaSourceBean;
import com.dasd412.api.diaryservice.adapter.out.persistence.diary.DiaryRepository;
import com.dasd412.api.diaryservice.application.service.UpdateDiaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UpdateDiaryServiceImpl implements UpdateDiaryService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DiaryRepository diaryRepository;

    private final KafkaSourceBean kafkaSourceBean;

    public UpdateDiaryServiceImpl(DiaryRepository diaryRepository, KafkaSourceBean kafkaSourceBean) {
        this.diaryRepository = diaryRepository;
        this.kafkaSourceBean = kafkaSourceBean;
    }

}
