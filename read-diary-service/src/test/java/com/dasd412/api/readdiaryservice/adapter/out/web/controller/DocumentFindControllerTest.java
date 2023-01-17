package com.dasd412.api.readdiaryservice.adapter.out.web.controller;

import com.dasd412.api.readdiaryservice.adapter.out.persistence.diary.DiaryDocumentRepository;
import com.dasd412.api.readdiaryservice.domain.diary.DiabetesDiaryDocument;
import com.dasd412.api.readdiaryservice.domain.diet.DietDocument;
import com.dasd412.api.readdiaryservice.domain.diet.EatTime;
import com.dasd412.api.readdiaryservice.domain.food.AmountUnit;
import com.dasd412.api.readdiaryservice.domain.food.FoodDocument;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "/application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class DocumentFindControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private DiaryDocumentRepository diaryDocumentRepository;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        if (mockMvc == null) {
            mockMvc = MockMvcBuilders
                    .webAppContextSetup(context)
                    .build();
        }
        DiabetesDiaryDocument createdOne = createDiaryDocumentOne();

        diaryDocumentRepository.save(createdOne);

        DiabetesDiaryDocument createdTwo = createDiaryDocumentTwo();

        diaryDocumentRepository.save(createdTwo);
    }

    @Test
    public void findAllFpg() throws Exception {
        String url = "/fpg/all";
        mockMvc.perform(get(url)
                .header("writer-id", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response").value(hasSize(2)));
    }

    @Test
    public void finalFpgBetween() {
    }

    @Test
    public void findAllBloodSugar() {
    }

    @Test
    public void findBloodSugarBetween() {
    }

    @Test
    public void findFoodList() {

    }

    private DiabetesDiaryDocument createDiaryDocumentOne() {
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
                .bloodSugar(170).eatTime(EatTime.BREAK_FAST).foodList(breakFast).build();

        DietDocument diet2 = DietDocument.builder().dietId(1L).diaryId(1L).writerId(1L)
                .bloodSugar(140).eatTime(EatTime.LUNCH).foodList(breakFast).build();

        DietDocument diet3 = DietDocument.builder().dietId(2L).diaryId(1L).writerId(1L)
                .bloodSugar(150).eatTime(EatTime.DINNER).foodList(dinner).build();

        List<DietDocument> dietList = Arrays.asList(diet1, diet2, diet3);

        return DiabetesDiaryDocument.builder().diaryId(1L).writerId(1L)
                .fastingPlasmaGlucose(120).remark("test")
                .writtenTime(LocalDateTime.now()).dietDocuments(dietList)
                .build();
    }

    private DiabetesDiaryDocument createDiaryDocumentTwo() {
        FoodDocument food1 = FoodDocument.builder()
                .foodId(1L).dietId(1L).writerId(1L).foodName("ham")
                .amount(110.0).amountUnit(AmountUnit.g).build();

        FoodDocument food2 = FoodDocument.builder()
                .foodId(1L).dietId(1L).writerId(1L).foodName("coffee")
                .amount(70.0).amountUnit(AmountUnit.mL).build();

        List<FoodDocument> breakFast = Arrays.asList(food1, food2);

        FoodDocument food3 = FoodDocument.builder()
                .foodId(1L).dietId(2L).writerId(1L).foodName("pizza")
                .amount(250.0).amountUnit(AmountUnit.g).build();

        FoodDocument food4 = FoodDocument.builder()
                .foodId(1L).dietId(2L).writerId(1L).foodName("water")
                .amount(200.0).amountUnit(AmountUnit.mL).build();

        List<FoodDocument> dinner = Arrays.asList(food3, food4);

        DietDocument diet1 = DietDocument.builder().dietId(1L).diaryId(1L).writerId(1L)
                .bloodSugar(120).eatTime(EatTime.BREAK_FAST).foodList(breakFast).build();

        DietDocument diet2 = DietDocument.builder().dietId(1L).diaryId(1L).writerId(1L)
                .bloodSugar(160).eatTime(EatTime.LUNCH).foodList(Collections.singletonList(food1)).build();

        DietDocument diet3 = DietDocument.builder().dietId(2L).diaryId(1L).writerId(1L)
                .bloodSugar(110).eatTime(EatTime.DINNER).foodList(dinner).build();

        List<DietDocument> dietList = Arrays.asList(diet1, diet2, diet3);

        return DiabetesDiaryDocument.builder().diaryId(2L).writerId(1L)
                .fastingPlasmaGlucose(100).remark("test")
                .writtenTime(LocalDateTime.now()).dietDocuments(dietList)
                .build();
    }
}