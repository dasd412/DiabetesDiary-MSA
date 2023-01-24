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
import com.querydsl.core.types.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.*;

import static com.dasd412.api.readdiaryservice.common.utils.date.DateStringConverter.isStartDateEqualOrBeforeEndDate;
import static com.google.common.base.Preconditions.checkArgument;

@Service
@Transactional(readOnly = true)
public class ReadDiaryServiceImpl implements ReadDiaryService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DiaryDocumentRepository diaryDocumentRepository;

    private final QDiabetesDiaryDocument qDocument = new QDiabetesDiaryDocument("diabetesDiaryDocument");

    private final QDietDocument qDietDocument = new QDietDocument("dietDocument");

    private final MongoTemplate mongoTemplate;

    public ReadDiaryServiceImpl(DiaryDocumentRepository diaryDocumentRepository, MongoTemplate mongoTemplate) {
        this.diaryDocumentRepository = diaryDocumentRepository;
        this.mongoTemplate = mongoTemplate;
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

        Pageable pageable = foodPageVO.makePageable();

        Query query = new Query(Criteria.where("writer_id").is(Long.parseLong(writerId)))
                .with(pageable)
                .with(Sort.by(Sort.Direction.DESC, "diet_list.blood_sugar", "written_time"));

        addCriteriaForTimeSpan(query,foodPageVO);

        addCriteriaForBloodSugar(query,foodPageVO);

        List<DiabetesDiaryDocument> diaryDocumentList = mongoTemplate.find(query, DiabetesDiaryDocument.class);

        return PageableExecutionUtils.getPage(
                diaryDocumentList,pageable,()->mongoTemplate.count(query.skip(-1).limit(-1),DiabetesDiaryDocument.class)
        );
    }

    private void addCriteriaForTimeSpan(Query query, FoodPageVO vo) {
        LocalDateTime startDate;

        LocalDateTime endDate;

        try {
            startDate = vo.convertStartDate().orElseThrow();

            endDate = vo.convertEndDate().orElseThrow();
        } catch (DateTimeException | NoSuchElementException e) {
            logger.info("It is invalid date format... So, this predicate should be ignored...");
            return;
        }

        if (isStartDateEqualOrBeforeEndDate(startDate, endDate)) {
            query.addCriteria(Criteria.where("written_time").gte(startDate).lt(endDate));
        }
    }

    private void addCriteriaForBloodSugar(Query query, FoodPageVO foodPageVO) {
        InequalitySign sign = foodPageVO.getEnumOfSign();

        int bloodSugar = foodPageVO.getBloodSugar();

        switch (sign) {
            case GREATER:
                query.addCriteria(Criteria.where("diet_list.blood_sugar").gt(bloodSugar));
                break;

            case LESSER:
                query.addCriteria(Criteria.where("diet_list.blood_sugar").lt(bloodSugar));
                break;

            case EQUAL:
                query.addCriteria(Criteria.where("diet_list.blood_sugar").is(bloodSugar));
                break;

            case GREAT_OR_EQUAL:
                query.addCriteria(Criteria.where("diet_list.blood_sugar").gte(bloodSugar));
                break;

            case LESSER_OR_EQUAL:
                query.addCriteria(Criteria.where("diet_list.blood_sugar").lte(bloodSugar));
                break;
        }
    }
}
