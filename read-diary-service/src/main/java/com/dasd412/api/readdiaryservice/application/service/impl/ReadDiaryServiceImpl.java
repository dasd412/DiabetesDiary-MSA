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

import com.dasd412.api.readdiaryservice.domain.food.FoodDocument;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        List<Predicate> predicates = new ArrayList<>();

        //1. id에 해당하는 작성자이고
        predicates.add(qDocument.writerId.eq(Long.parseLong(writerId)));

        //2. 작성 기간 안에 쓰여진
        addPredicateForTimeSpan(predicates, foodPageVO);

        //3. 일지를 모두 찾는다.
        List<DiabetesDiaryDocument> diaryDocumentList = (List<DiabetesDiaryDocument>) diaryDocumentRepository.findAll(ExpressionUtils.allOf(predicates));

        //4. 식사 혈당 필터 조건이 있다면 필터링 한다. 그리고 필터링한 식단과 음식을 모아 DTO로 만든다.
        List<FoodBoardDTO> foodBoardDTOList = filterWithEqualitySignAndBloodSugar(diaryDocumentList, foodPageVO.getEnumOfSign(), foodPageVO.getBloodSugar());

        //5. DTO를 조건 순으로 정렬한다.
        foodBoardDTOList.sort(Comparator
                .comparing(FoodBoardDTO::getBloodSugar, Comparator.reverseOrder())
                .thenComparing(FoodBoardDTO::getWrittenTime, Comparator.reverseOrder())
                .thenComparing(FoodBoardDTO::getFoodName, Comparator.naturalOrder()));

        //6. 5.의 결과를 페이징한다.

        return null;
    }


    private void addPredicateForTimeSpan(List<Predicate> predicates, FoodPageVO vo) {
        LocalDateTime startDate;

        LocalDateTime endDate;

        try {
            startDate = vo.convertStartDate().orElseThrow(IllegalArgumentException::new);

            endDate = vo.convertEndDate().orElseThrow(IllegalArgumentException::new);
        } catch (IllegalArgumentException e) {
            logger.info("It is invalid date format... So, this predicate should be ignored...");
            return;
        }

        if (isStartDateEqualOrBeforeEndDate(startDate, endDate)) {
            predicates.add(qDocument.writtenTime.between(startDate, endDate));
        }
    }

    private List<FoodBoardDTO> filterWithEqualitySignAndBloodSugar(List<DiabetesDiaryDocument> diaryDocumentList, InequalitySign sign, int bloodSugar) {

        List<FoodBoardDTO> dtoList = new ArrayList<>();

        for (DiabetesDiaryDocument diaryDocument : diaryDocumentList) {
            for (DietDocument dietDocument : diaryDocument.getDietList()) {
                if (!isRightCondition(dietDocument, sign, bloodSugar)) {
                    continue;
                }
                for (FoodDocument foodDocument : dietDocument.getFoodList()) {
                    dtoList.add(new FoodBoardDTO(foodDocument.getFoodName(), dietDocument.getBloodSugar(), diaryDocument.getWrittenTime(), diaryDocument.getDiaryId()));
                }
            }
        }

        return dtoList;
    }

    private boolean isRightCondition(DietDocument dietDocument, InequalitySign sign, int bloodSugar) {
        switch (sign) {
            case GREATER:
                return dietDocument.getBloodSugar() > bloodSugar;

            case LESSER:
                return dietDocument.getBloodSugar() < bloodSugar;

            case EQUAL:
                return dietDocument.getBloodSugar() == bloodSugar;

            case GREAT_OR_EQUAL:
                return dietDocument.getBloodSugar() >= bloodSugar;

            case LESSER_OR_EQUAL:
                return dietDocument.getBloodSugar() <= bloodSugar;
        }
        // 조건이 없으면 다 dto로 담아야 하므로 true를 반환.
        return true;
    }
}
