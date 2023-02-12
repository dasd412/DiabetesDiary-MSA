package com.dasd412.api.readdiaryservice.application.service.impl;

import com.dasd412.api.readdiaryservice.adapter.in.msessage.model.diary.dto.DiaryToReaderDTO;
import com.dasd412.api.readdiaryservice.adapter.in.msessage.model.diary.dto.DietToReaderDTO;
import com.dasd412.api.readdiaryservice.adapter.in.msessage.model.diary.dto.FoodToReaderDTO;
import com.dasd412.api.readdiaryservice.adapter.out.persistence.diary.DiaryDocumentRepository;
import com.dasd412.api.readdiaryservice.application.service.DiaryDataSyncService;
import com.dasd412.api.readdiaryservice.domain.diary.DiabetesDiaryDocument;
import com.dasd412.api.readdiaryservice.domain.diet.EatTime;
import com.dasd412.api.readdiaryservice.domain.food.AmountUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "/application-test.properties")
public class DiaryDataSyncServiceTest {

    @Autowired
    private DiaryDataSyncService diaryDataSyncService;

    @Autowired
    private DiaryDocumentRepository diaryDocumentRepository;

    private DiaryToReaderDTO readerDTO;

    @Before
    public void setUp() {
        FoodToReaderDTO foodToReaderDTO = FoodToReaderDTO.builder()
                .foodId(111L).dietId(111L).writerId(1L)
                .foodName("toast").amount(55.9).amountUnit(AmountUnit.g)
                .build();

        DietToReaderDTO dietToReaderDTO = DietToReaderDTO.builder()
                .dietId(111L).diaryId(111L).writerId(1L)
                .eatTime(EatTime.DINNER).bloodSugar(100)
                .foodList(Collections.singletonList(foodToReaderDTO))
                .build();

        this.readerDTO = DiaryToReaderDTO.builder()
                .diaryId(111L).writerId(1L)
                .fastingPlasmaGlucose(100).remark("test")
                .writtenTime(LocalDateTime.now())
                .dietToReaderDTOList(Collections.singletonList(dietToReaderDTO)).build();
    }

    @After
    public void clean() {
        this.diaryDocumentRepository.deleteAll();
    }

    @Test
    public void createDocument() {
        diaryDataSyncService.createDocument(readerDTO);

        List<DiabetesDiaryDocument> diaryDocumentList = diaryDocumentRepository.findAll();

        assertThat(diaryDocumentList.size()).isEqualTo(1);
        assertThat(diaryDocumentList.get(0).getDiaryId()).isEqualTo(readerDTO.getDiaryId());
    }

    @Test
    public void updateDocument() {
        diaryDataSyncService.createDocument(readerDTO);

        DiabetesDiaryDocument targetDiary = diaryDocumentRepository.findById(readerDTO.getDiaryId()).orElseThrow();

        DietToReaderDTO dietToReaderDTO = DietToReaderDTO.builder()
                .dietId(111L).diaryId(111L).writerId(1L)
                .eatTime(EatTime.LUNCH).bloodSugar(100)
                .foodList(Collections.emptyList())
                .build();

        DiaryToReaderDTO forUpdate = DiaryToReaderDTO.builder()
                .diaryId(111L).writerId(1L)
                .fastingPlasmaGlucose(120).remark("test")
                .writtenTime(LocalDateTime.now())
                .dietToReaderDTOList(Collections.singletonList(dietToReaderDTO)).build();

        diaryDataSyncService.updateDocument(targetDiary, forUpdate);

        DiabetesDiaryDocument updated = diaryDocumentRepository.findById(readerDTO.getDiaryId()).orElseThrow();

        assertThat(updated.getFastingPlasmaGlucose()).isEqualTo(forUpdate.getFastingPlasmaGlucose());
        assertThat(updated.getDietList().get(0).getEatTime()).isEqualTo(EatTime.LUNCH);
    }

    @Test
    public void deleteDocument() {
        diaryDataSyncService.createDocument(readerDTO);

        DiabetesDiaryDocument targetDiary = diaryDocumentRepository.findById(readerDTO.getDiaryId()).orElseThrow();

        diaryDataSyncService.deleteDocument(targetDiary);

        List<DiabetesDiaryDocument> diaryDocumentList = diaryDocumentRepository.findAll();

        assertThat(diaryDocumentList.size()).isEqualTo(0);
    }

    @Test
    public void deleteAllOfWriter() {
        LongStream.range(1, 6).forEach(
                i -> {
                    DiaryToReaderDTO forCreate = DiaryToReaderDTO.builder()
                            .diaryId(i).writerId(1L)
                            .fastingPlasmaGlucose(100).remark("test")
                            .writtenTime(LocalDateTime.now())
                            .dietToReaderDTOList(Collections.emptyList()).build();

                    diaryDataSyncService.createDocument(forCreate);
                }
        );

        List<DiabetesDiaryDocument> diaryDocumentList = diaryDocumentRepository.findAll();

        assertThat(diaryDocumentList.size()).isEqualTo(5);

        diaryDataSyncService.deleteAllOfWriter(1L);

        assertThat(diaryDocumentRepository.findAll().size()).isEqualTo(0);
    }
}