package com.dasd412.api.readdiaryservice.adapter.out.web.controller;

import com.dasd412.api.readdiaryservice.adapter.out.persistence.diary.DiaryDocumentRepository;
import com.dasd412.api.readdiaryservice.domain.diary.DiabetesDiaryDocument;
import com.dasd412.api.readdiaryservice.domain.diet.DietDocument;
import com.dasd412.api.readdiaryservice.domain.diet.EatTime;
import com.dasd412.api.readdiaryservice.domain.food.AmountUnit;
import com.dasd412.api.readdiaryservice.domain.food.FoodDocument;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "/application-test.properties")
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

    @After
    public void clean() {
        this.diaryDocumentRepository.deleteAll();
    }

    @Test
    public void findOneDiaryDocument() throws Exception {
        String url = "/diary/1";

        mockMvc.perform(get(url).header("writer-id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"));
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
    public void findFpgBetweenInvalidTimeSpan() throws Exception {
        String url = "/fpg/between";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("startYear", "2023");
        params.add("startMonth", "01");
        params.add("startDay", "28");

        params.add("endYear", "2023");
        params.add("endMonth", "01");
        params.add("endDay", "17");

        mockMvc.perform(get(url).header("writer-id", "1")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.error.status").value("400"));
    }

    @Test
    public void findFpgBetween() throws Exception {
        String url = "/fpg/between";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("startYear", "2023");
        params.add("startMonth", "01");
        params.add("startDay", "14");

        params.add("endYear", "2023");
        params.add("endMonth", "01");
        params.add("endDay", "28");

        mockMvc.perform(get(url).header("writer-id", "1")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response").value(hasSize(1)));
    }

    @Test
    public void findAllBloodSugar() throws Exception {
        String url = "/blood-sugar/all";

        mockMvc.perform(get(url)
                        .header("writer-id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response").value(hasSize(4)));
    }

    @Test
    public void findBloodSugarBetweenInvalidTimeSpan() throws Exception {
        String url = "/blood-sugar/between";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("startYear", "2023");
        params.add("startMonth", "01");
        params.add("startDay", "28");

        params.add("endYear", "2023");
        params.add("endMonth", "01");
        params.add("endDay", "17");

        mockMvc.perform(get(url).header("writer-id", "1")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.error.status").value("400"));
    }

    @Test
    public void findBloodSugarBetween() throws Exception {
        String url = "/blood-sugar/between";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("startYear", "2023");
        params.add("startMonth", "01");
        params.add("startDay", "14");

        params.add("endYear", "2023");
        params.add("endMonth", "01");
        params.add("endDay", "28");

        mockMvc.perform(get(url).header("writer-id", "1")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response").value(hasSize(2)));
    }

    private DiabetesDiaryDocument createDiaryDocumentOne() {
        FoodDocument food1 = FoodDocument.builder()
                .foodId(1L).dietId(1L).writerId(1L).foodName("Toast")
                .amount(50.0).amountUnit(AmountUnit.g).build();

        FoodDocument food2 = FoodDocument.builder()
                .foodId(2L).dietId(1L).writerId(1L).foodName("juice")
                .amount(60.0).amountUnit(AmountUnit.mL).build();

        List<FoodDocument> breakFast = Arrays.asList(food1, food2);

        FoodDocument food3 = FoodDocument.builder()
                .foodId(3L).dietId(2L).writerId(1L).foodName("chicken")
                .amount(150.0).amountUnit(AmountUnit.g).build();

        FoodDocument food4 = FoodDocument.builder()
                .foodId(4L).dietId(2L).writerId(1L).foodName("coke")
                .amount(100.0).amountUnit(AmountUnit.mL).build();

        List<FoodDocument> dinner = Arrays.asList(food3, food4);

        DietDocument diet1 = DietDocument.builder().dietId(1L).diaryId(1L).writerId(1L)
                .bloodSugar(170).eatTime(EatTime.BREAK_FAST).foodList(breakFast).build();

        DietDocument diet2 = DietDocument.builder().dietId(2L).diaryId(1L).writerId(1L)
                .bloodSugar(150).eatTime(EatTime.DINNER).foodList(dinner).build();

        List<DietDocument> dietList = Arrays.asList(diet1, diet2);

        return DiabetesDiaryDocument.builder().diaryId(1L).writerId(1L)
                .fastingPlasmaGlucose(120).remark("test")
                .writtenTime(LocalDateTime.of(2023, 1, 11, 7, 7)).dietDocuments(dietList)
                .build();
    }

    private DiabetesDiaryDocument createDiaryDocumentTwo() {
        FoodDocument food1 = FoodDocument.builder()
                .foodId(5L).dietId(4L).writerId(1L).foodName("ham")
                .amount(110.0).amountUnit(AmountUnit.g).build();

        FoodDocument food2 = FoodDocument.builder()
                .foodId(6L).dietId(4L).writerId(1L).foodName("coffee")
                .amount(70.0).amountUnit(AmountUnit.mL).build();

        List<FoodDocument> breakFast = Arrays.asList(food1, food2);

        FoodDocument food3 = FoodDocument.builder()
                .foodId(7L).dietId(5L).writerId(1L).foodName("pizza")
                .amount(250.0).amountUnit(AmountUnit.g).build();

        FoodDocument food4 = FoodDocument.builder()
                .foodId(8L).dietId(5L).writerId(1L).foodName("water")
                .amount(200.0).amountUnit(AmountUnit.mL).build();

        List<FoodDocument> dinner = Arrays.asList(food3, food4);

        DietDocument diet4 = DietDocument.builder().dietId(4L).diaryId(2L).writerId(1L)
                .bloodSugar(120).eatTime(EatTime.BREAK_FAST).foodList(breakFast).build();


        DietDocument diet5 = DietDocument.builder().dietId(5L).diaryId(2L).writerId(1L)
                .bloodSugar(110).eatTime(EatTime.DINNER).foodList(dinner).build();

        List<DietDocument> dietList = Arrays.asList(diet4, diet5);

        return DiabetesDiaryDocument.builder().diaryId(2L).writerId(1L)
                .fastingPlasmaGlucose(100).remark("test")
                .writtenTime(LocalDateTime.of(2023, 1, 15, 6, 6)).dietDocuments(dietList)
                .build();
    }
}