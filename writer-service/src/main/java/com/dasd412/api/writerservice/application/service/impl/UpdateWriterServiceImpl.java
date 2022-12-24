package com.dasd412.api.writerservice.application.service.impl;

import com.dasd412.api.writerservice.application.service.UpdateWriterService;
import com.dasd412.api.writerservice.domain.writer.Writer;
import com.dasd412.api.writerservice.adapter.out.persistence.WriterRepository;
import com.dasd412.api.writerservice.common.utils.UserContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;

@Service
public class UpdateWriterServiceImpl implements UpdateWriterService {

    private final WriterRepository writerRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public UpdateWriterServiceImpl(WriterRepository writerRepository) {
        this.writerRepository = writerRepository;
    }

    //todo attachRelationWithDiary()와 detachRelationWithDiary()는 추후에 레디스 도입하면 바꿀 예정...
    @Transactional
    public void attachRelationWithDiary(Long writerId, Long diaryId) {
        logger.debug("attaching relation of writer {} with diary {} in correlation id {}.", writerId, diaryId, UserContext.getCorrelationId());
        Writer writer = writerRepository.findById(writerId).orElseThrow((NoResultException::new));
        writer.addDiary(diaryId);
        writerRepository.save(writer);
    }

    @Transactional
    public void detachRelationWithDiary(Long writerId, Long diaryId) {
        logger.debug("detaching relation of writer {} with diary {} in correlation id {}.", writerId, diaryId, UserContext.getCorrelationId());
        Writer writer = writerRepository.findById(writerId).orElseThrow((NoResultException::new));
        writer.removeDiary(diaryId);
        writerRepository.save(writer);
    }
}
