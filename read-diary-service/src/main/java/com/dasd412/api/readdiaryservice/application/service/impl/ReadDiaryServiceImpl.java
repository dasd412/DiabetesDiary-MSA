package com.dasd412.api.readdiaryservice.application.service.impl;

import com.dasd412.api.readdiaryservice.adapter.out.persistence.diary.DiaryDocumentRepository;
import com.dasd412.api.readdiaryservice.application.service.ReadDiaryService;
import com.dasd412.api.readdiaryservice.common.utils.date.DateStringConverter;
import com.dasd412.api.readdiaryservice.common.utils.trace.UserContextHolder;
import com.dasd412.api.readdiaryservice.domain.diary.DiabetesDiaryDocument;
import com.dasd412.api.readdiaryservice.domain.diary.QDiabetesDiaryDocument;
import com.querydsl.core.types.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

@Service
@Transactional(readOnly = true)
public class ReadDiaryServiceImpl implements ReadDiaryService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DiaryDocumentRepository diaryDocumentRepository;

    private final QDiabetesDiaryDocument qDocument = new QDiabetesDiaryDocument("diabetesDiaryDocument");

    public ReadDiaryServiceImpl(DiaryDocumentRepository diaryDocumentRepository) {
        this.diaryDocumentRepository = diaryDocumentRepository;
    }

    @Override
    public List<DiabetesDiaryDocument> getDiabetesDiariesOfWriter(String writerId) {
        logger.info("find all fpg in ReadDiaryService : {} ", UserContextHolder.getContext().getCorrelationId());

        Predicate predicate = qDocument.writerId.eq(Long.parseLong(writerId));

        return (List<DiabetesDiaryDocument>) diaryDocumentRepository.findAll(predicate);
    }

    @Override
    public List<DiabetesDiaryDocument> getDiariesBetweenTimeSpan(String writerId, Map<String, String> timeSpan) {
        logger.info("find fpg between time span in ReadDiaryService : {} ", UserContextHolder.getContext().getCorrelationId());

        LocalDateTime startDate = DateStringConverter.convertMapParamsToStartDate(timeSpan);
        LocalDateTime endDate = DateStringConverter.convertMapParamsToEndDate(timeSpan);

        checkArgument(DateStringConverter.isStartDateEqualOrBeforeEndDate(startDate, endDate), "startDate must be equal or before than endDate");

        Predicate predicate = qDocument.writerId.eq(Long.parseLong(writerId))
                .and(qDocument.writtenTime.between(startDate, endDate));

        return (List<DiabetesDiaryDocument>) diaryDocumentRepository.findAll(predicate);
    }
}
