package com.dasd412.api.readdiaryservice.application.service.impl;

import com.dasd412.api.readdiaryservice.adapter.in.web.FoodPageVO;
import com.dasd412.api.readdiaryservice.adapter.in.web.InequalitySign;
import com.dasd412.api.readdiaryservice.adapter.out.persistence.diary.DiaryDocumentRepository;
import com.dasd412.api.readdiaryservice.adapter.out.web.dto.AllBloodSugarDTO;
import com.dasd412.api.readdiaryservice.adapter.out.web.dto.BloodSugarBetweenTimeSpanDTO;
import com.dasd412.api.readdiaryservice.adapter.out.web.dto.FoodBoardDTO;
import com.dasd412.api.readdiaryservice.application.service.ReadDiaryService;
import com.dasd412.api.readdiaryservice.common.utils.date.DateStringConverter;
import com.dasd412.api.readdiaryservice.common.utils.trace.UserContextHolder;
import com.dasd412.api.readdiaryservice.domain.diary.DiabetesDiaryDocument;
import com.dasd412.api.readdiaryservice.domain.diary.QDiabetesDiaryDocument;
import com.dasd412.api.readdiaryservice.domain.diet.DietDocument;
import com.dasd412.api.readdiaryservice.domain.diet.QDietDocument;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.dasd412.api.readdiaryservice.common.utils.date.DateStringConverter.isStartDateEqualOrBeforeEndDate;
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

        checkArgument(isStartDateEqualOrBeforeEndDate(startDate, endDate), "startDate must be equal or before than endDate");

        Predicate predicate = qDocument.writerId.eq(Long.parseLong(writerId))
                .and(qDocument.writtenTime.between(startDate, endDate));

        return (List<DiabetesDiaryDocument>) diaryDocumentRepository.findAll(predicate);
    }

    @Override
    public List<AllBloodSugarDTO> getAllBloodSugarOfWriter(String writerId) {
        logger.info("find all blood sugar in ReadDiaryService : {} ", UserContextHolder.getContext().getCorrelationId());

        Predicate predicate = qDocument.writerId.eq(Long.parseLong(writerId));

        List<DiabetesDiaryDocument> diaryDocumentList = (List<DiabetesDiaryDocument>) diaryDocumentRepository.findAll(predicate);

        List<AllBloodSugarDTO> dtoList = new ArrayList<>();

        for (DiabetesDiaryDocument diaryDocument : diaryDocumentList) {
            for (DietDocument dietDocument : diaryDocument.getDietList()) {
                dtoList.add(new AllBloodSugarDTO(diaryDocument, dietDocument));
            }
        }

        dtoList.sort(Comparator.comparing(AllBloodSugarDTO::getDateTime));

        return dtoList;
    }

    @Override
    public List<BloodSugarBetweenTimeSpanDTO> getBloodSugarBetweenTimeSpan(String writerId, Map<String, String> timeSpan) {
        logger.info("find blood sugar between time span in ReadDiaryService : {} ", UserContextHolder.getContext().getCorrelationId());

        LocalDateTime startDate = DateStringConverter.convertMapParamsToStartDate(timeSpan);

        LocalDateTime endDate = DateStringConverter.convertMapParamsToEndDate(timeSpan);

        checkArgument(isStartDateEqualOrBeforeEndDate(startDate, endDate), "startDate must be equal or before than endDate");

        Predicate predicate = qDocument.writerId.eq(Long.parseLong(writerId))
                .and(qDocument.writtenTime.between(startDate, endDate));

        List<DiabetesDiaryDocument> diaryDocumentList = (List<DiabetesDiaryDocument>) diaryDocumentRepository.findAll(predicate);

        List<BloodSugarBetweenTimeSpanDTO> dtoList = new ArrayList<>();

        for (DiabetesDiaryDocument diaryDocument : diaryDocumentList) {
            for (DietDocument dietDocument : diaryDocument.getDietList()) {
                dtoList.add(new BloodSugarBetweenTimeSpanDTO(diaryDocument, dietDocument));
            }
        }

        dtoList.sort(Comparator.comparing(BloodSugarBetweenTimeSpanDTO::getBloodSugar));

        return dtoList;
    }

    @Override
    public Page<FoodBoardDTO> getFoodByPagination(String writerId, FoodPageVO foodPageVO) {
        logger.info("find food board in ReadDiaryService : {} ", UserContextHolder.getContext().getCorrelationId());

        return null;
    }

    private BooleanBuilder decideEqualitySignOfBloodSugar(InequalitySign sign, int bloodSugar) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        switch (sign) {
            case GREATER:
                booleanBuilder.and(QDietDocument.dietDocument.bloodSugar.gt(bloodSugar));
                break;

            case LESSER:
                booleanBuilder.and(QDietDocument.dietDocument.bloodSugar.lt(bloodSugar));
                break;

            case EQUAL:
                booleanBuilder.and(QDietDocument.dietDocument.bloodSugar.eq(bloodSugar));
                break;

            case GREAT_OR_EQUAL:
                booleanBuilder.and(QDietDocument.dietDocument.bloodSugar.goe(bloodSugar));
                break;

            case LESSER_OR_EQUAL:
                booleanBuilder.and(QDietDocument.dietDocument.bloodSugar.loe(bloodSugar));
                break;
        }

        return booleanBuilder;
    }
}
