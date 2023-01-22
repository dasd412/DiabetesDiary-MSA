package com.dasd412.api.readdiaryservice.application.service.impl;

import com.dasd412.api.readdiaryservice.adapter.in.web.FoodPageVO;
import com.dasd412.api.readdiaryservice.adapter.in.web.InequalitySign;
import com.dasd412.api.readdiaryservice.adapter.out.persistence.diary.DiaryDocumentRepository;
import com.dasd412.api.readdiaryservice.adapter.out.web.dto.AllBloodSugarDTO;
import com.dasd412.api.readdiaryservice.adapter.out.web.dto.BloodSugarBetweenTimeSpanDTO;
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
    public Page<DiabetesDiaryDocument> getFoodByPagination(String writerId, FoodPageVO foodPageVO) {
        logger.info("find food board in ReadDiaryService : {} ", UserContextHolder.getContext().getCorrelationId());

        Pageable pageable = makePageableWithSort(foodPageVO);

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(qDocument.writerId.eq(Long.parseLong(writerId)));

        if (foodPageVO.getSign() != null && foodPageVO.getEnumOfSign() != InequalitySign.NONE) {
            predicates.add(decideEqualitySignOfBloodSugar(foodPageVO.getEnumOfSign(), foodPageVO.getBloodSugar()));
        }

        LocalDateTime startDate;

        LocalDateTime endDate;

        try {
            startDate = foodPageVO.convertStartDate().orElseThrow(() -> new DateTimeException("start date cannot convert"));

            endDate = foodPageVO.convertEndDate().orElseThrow(() -> new DateTimeException("end date cannot convert"));

        } catch (DateTimeException e) {
            // 날짜 변환이 안되면 날짜 조건 없이 질의.
            return diaryDocumentRepository.findAll(ExpressionUtils.allOf(predicates),pageable);
        }

        if (isStartDateEqualOrBeforeEndDate(startDate, endDate)) {
            predicates.add(qDocument.writtenTime.between(startDate, endDate));
        }


        return diaryDocumentRepository.findAll(ExpressionUtils.allOf(predicates),pageable);
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

    private Pageable makePageableWithSort(FoodPageVO foodPageVO) {
        // Sort.by 내 인자는 document 엔티티 내의 @Field 값과 동일해야 한다.
        Sort sort = Sort.by("blood_sugar").descending()
                .and(Sort.by("written_time").descending().and(Sort.by("food_name")).ascending());

        return PageRequest.of(foodPageVO.getPage() - 1, foodPageVO.getSize(), sort);
    }
}
