package com.dasd412.api.readdiaryservice.application.service.impl;

import com.dasd412.api.readdiaryservice.adapter.in.msessage.model.diary.dto.DiaryToReaderDTO;
import com.dasd412.api.readdiaryservice.adapter.out.persistence.diary.DiaryDocumentRepository;
import com.dasd412.api.readdiaryservice.application.service.DiaryDataSyncService;
import com.dasd412.api.readdiaryservice.common.utils.trace.UserContextHolder;
import com.dasd412.api.readdiaryservice.domain.diary.DiabetesDiaryDocument;
import com.dasd412.api.readdiaryservice.domain.diet.DietDocument;
import com.dasd412.api.readdiaryservice.domain.food.FoodDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DiaryDataSyncServiceImpl implements DiaryDataSyncService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DiaryDocumentRepository diaryDocumentRepository;

    public DiaryDataSyncServiceImpl(DiaryDocumentRepository diaryDocumentRepository) {
        this.diaryDocumentRepository = diaryDocumentRepository;
    }

    @Override
    public void createDocument(DiaryToReaderDTO diaryToReaderDTO) {
        logger.info("creating document in DiaryDataSyncService : {} ", UserContextHolder.getContext().getCorrelationId());

        List<DietDocument> dietDocumentList = new ArrayList<>();

        diaryToReaderDTO.getDietList().forEach(

                dietToReaderDTO -> {

                    List<FoodDocument> foodDocumentList = new ArrayList<>();

                    dietToReaderDTO.getFoodList().forEach(
                            foodToReaderDTO -> foodDocumentList.add(foodToReaderDTO.toEntity())
                    );

                    dietDocumentList.add(dietToReaderDTO.toEntity(foodDocumentList));
                }
        );

        DiabetesDiaryDocument diaryDocument = diaryToReaderDTO.toEntity(dietDocumentList);

        diaryDocumentRepository.save(diaryDocument);
    }
}
