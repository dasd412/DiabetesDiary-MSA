package com.dasd412.api.readdiaryservice.adapter.out.persistence;

import com.dasd412.api.readdiaryservice.adapter.out.persistence.diary.DiaryDocumentRepository;
import com.dasd412.api.readdiaryservice.domain.diary.DiabetesDiaryDocument;
import com.dasd412.api.readdiaryservice.domain.diet.DietDocument;
import com.dasd412.api.readdiaryservice.domain.diet.EatTime;
import com.dasd412.api.readdiaryservice.domain.food.AmountUnit;
import com.dasd412.api.readdiaryservice.domain.food.FoodDocument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataMongoTest
@TestPropertySource(locations = "/application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MongoDBBasicCrudTest {

    @Autowired
    private DiaryDocumentRepository diaryDocumentRepository;

    @Test
    public void insertDiary() {
        DiabetesDiaryDocument created = createDiaryDocument();

        created = diaryDocumentRepository.save(created);

        List<DietDocument> dietDocuments = created.getDietList();

        assertThat(diaryDocumentRepository.findAll().size()).isEqualTo(1);

        assertThat(dietDocuments.size()).isEqualTo(2);

        List<FoodDocument> foodDocuments = dietDocuments.stream()
                .flatMap(list -> list.getFoodList().stream())
                .collect(Collectors.toList());

        assertThat(foodDocuments.size()).isEqualTo(4);
    }

    @Test
    public void updateDiary() {
        DiabetesDiaryDocument created = createDiaryDocument();

        created = diaryDocumentRepository.save(created);

        created.update(150, "test1");

        created = diaryDocumentRepository.save(created);

        Long diaryId = created.getDiaryId();

        DiabetesDiaryDocument found = diaryDocumentRepository.findById(diaryId).orElseThrow(IllegalArgumentException::new);

        assertThat(found.getFastingPlasmaGlucose()).isEqualTo(150);
        assertThat(found.getRemark()).isEqualTo("test1");
    }

    @Test
    public void deleteDiary() {
        DiabetesDiaryDocument created = createDiaryDocument();

        created = diaryDocumentRepository.save(created);

        diaryDocumentRepository.deleteById(created.getDiaryId());

        assertThat(diaryDocumentRepository.findAll().size()).isEqualTo(0);
    }

    private DiabetesDiaryDocument createDiaryDocument() {
        FoodDocument food1 = FoodDocument.builder()
                .foodId(1L).dietId(1L).writerId(1L).foodName("Toast")
                .amount(50.0).amountUnit(AmountUnit.g).build();

        FoodDocument food2 = FoodDocument.builder()
                .foodId(1L).dietId(1L).writerId(1L).foodName("juice")
                .amount(60.0).amountUnit(AmountUnit.mL).build();

        List<FoodDocument> breakFast = Arrays.asList(food1, food2);

        FoodDocument food3 = FoodDocument.builder()
                .foodId(1L).dietId(2L).writerId(1L).foodName("chicken")
                .amount(150.0).amountUnit(AmountUnit.g).build();

        FoodDocument food4 = FoodDocument.builder()
                .foodId(1L).dietId(2L).writerId(1L).foodName("coke")
                .amount(100.0).amountUnit(AmountUnit.mL).build();

        List<FoodDocument> dinner = Arrays.asList(food3, food4);

        DietDocument diet1 = DietDocument.builder().dietId(1L).diaryId(1L).writerId(1L)
                .bloodSugar(100).eatTime(EatTime.BREAK_FAST).foodList(breakFast).build();

        DietDocument diet2 = DietDocument.builder().dietId(2L).diaryId(1L).writerId(1L)
                .bloodSugar(150).eatTime(EatTime.DINNER).foodList(dinner).build();

        List<DietDocument> dietList = Arrays.asList(diet1, diet2);

        return DiabetesDiaryDocument.builder().diaryId(1L).writerId(1L)
                .fastingPlasmaGlucose(120).remark("test")
                .writtenTime(LocalDateTime.now()).dietDocuments(dietList)
                .build();
    }
}
