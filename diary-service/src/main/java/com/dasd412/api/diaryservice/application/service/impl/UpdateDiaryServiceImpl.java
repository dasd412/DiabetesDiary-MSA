package com.dasd412.api.diaryservice.application.service.impl;

import com.dasd412.api.diaryservice.adapter.in.web.dto.update.DiaryUpdateRequestDTO;
import com.dasd412.api.diaryservice.adapter.out.client.FindWriterFeignClient;
import com.dasd412.api.diaryservice.adapter.out.message.source.KafkaSourceBean;
import com.dasd412.api.diaryservice.adapter.out.persistence.diary.DiaryRepository;
import com.dasd412.api.diaryservice.application.service.UpdateDiaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeoutException;

@Service
public class UpdateDiaryServiceImpl implements UpdateDiaryService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DiaryRepository diaryRepository;

    //todo 이 동기식 검증이 JWT 도입 이후에도 필요할지는 고민 필요.
    private final FindWriterFeignClient findWriterFeignClient;

    private final KafkaSourceBean kafkaSourceBean;

    public UpdateDiaryServiceImpl(DiaryRepository diaryRepository, FindWriterFeignClient findWriterFeignClient, KafkaSourceBean kafkaSourceBean) {
        this.diaryRepository = diaryRepository;
        this.findWriterFeignClient = findWriterFeignClient;
        this.kafkaSourceBean = kafkaSourceBean;
    }

    @Override
    public Long updateDiaryWithEntities(DiaryUpdateRequestDTO dto) throws TimeoutException {
        return null;
    }
}
