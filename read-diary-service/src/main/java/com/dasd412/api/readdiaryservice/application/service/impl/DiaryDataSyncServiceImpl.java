package com.dasd412.api.readdiaryservice.application.service.impl;

import com.dasd412.api.readdiaryservice.adapter.in.msessage.model.diary.dto.DiaryToReaderDTO;
import com.dasd412.api.readdiaryservice.adapter.in.msessage.model.diary.dto.DietToReaderDTO;
import com.dasd412.api.readdiaryservice.adapter.in.msessage.model.diary.dto.FoodToReaderDTO;
import com.dasd412.api.readdiaryservice.adapter.out.persistence.diary.DiaryDocumentRepository;
import com.dasd412.api.readdiaryservice.application.service.DiaryDataSyncService;
import com.dasd412.api.readdiaryservice.domain.diary.DiabetesDiaryDocument;
import com.dasd412.api.readdiaryservice.domain.diet.DietDocument;
import com.dasd412.api.readdiaryservice.domain.food.FoodDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class DiaryDataSyncServiceImpl implements DiaryDataSyncService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DiaryDocumentRepository diaryDocumentRepository;

    public DiaryDataSyncServiceImpl(DiaryDocumentRepository diaryDocumentRepository) {
        this.diaryDocumentRepository = diaryDocumentRepository;
    }

    @Override
    public void createDocument(DiaryToReaderDTO diaryToReaderDTO) {
        logger.info("creating document in DiaryDataSyncService");

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

    @Override
    public void updateDocument(DiabetesDiaryDocument targetDiary, DiaryToReaderDTO diaryToReaderDTO) {
        logger.info("updating document in DiaryDataSyncService");

        targetDiary.update(diaryToReaderDTO.getFastingPlasmaGlucose(), diaryToReaderDTO.getRemark());

        for (DietDocument dietDocument : targetDiary.getDietList()) {
            for (DietToReaderDTO dietToReaderDTO : diaryToReaderDTO.getDietList()) {
                if (Objects.equals(dietDocument.getDietId(), dietToReaderDTO.getDietId())) {
                    dietDocument.update(dietToReaderDTO.getEatTime(), dietToReaderDTO.getBloodSugar());
                }

                for (FoodDocument foodDocument : dietDocument.getFoodList()) {
                    for (FoodToReaderDTO foodToReaderDTO : dietToReaderDTO.getFoodList()) {
                        if (Objects.equals(foodDocument.getFoodId(), foodToReaderDTO.getFoodId())) {
                            foodDocument.update(foodToReaderDTO.getFoodName(), foodToReaderDTO.getAmount(), foodToReaderDTO.getAmountUnit());
                        }
                    }
                }
            }
        }

        diaryDocumentRepository.save(targetDiary);
    }

    @Override
    public void deleteDocument(DiabetesDiaryDocument targetDiary) {
        logger.info("deleting document in DiaryDataSyncService");

        diaryDocumentRepository.delete(targetDiary);
    }
}
