package com.dasd412.api.diaryservice.adapter.in.web;

import com.dasd412.api.diaryservice.DiaryServiceApplication;
import com.dasd412.api.diaryservice.adapter.in.web.dto.post.DiaryPostRequestDTO;
import com.dasd412.api.diaryservice.adapter.in.web.dto.post.DietPostRequestDTO;
import com.dasd412.api.diaryservice.adapter.in.web.dto.post.FoodPostRequestDTO;
import com.dasd412.api.diaryservice.adapter.in.web.dto.update.DiaryUpdateRequestDTO;
import com.dasd412.api.diaryservice.adapter.in.web.dto.update.DietUpdateRequestDTO;
import com.dasd412.api.diaryservice.adapter.in.web.dto.update.FoodUpdateRequestDTO;
import com.dasd412.api.diaryservice.adapter.out.client.FindWriterFeignClient;
import com.dasd412.api.diaryservice.adapter.out.message.source.KafkaSourceBean;
import com.dasd412.api.diaryservice.adapter.out.persistence.diary.DiaryRepository;
import com.dasd412.api.diaryservice.adapter.out.persistence.diet.DietRepository;
import com.dasd412.api.diaryservice.adapter.out.persistence.food.FoodRepository;
import com.dasd412.api.diaryservice.application.service.UpdateDiaryService;
import com.dasd412.api.diaryservice.domain.diary.DiabetesDiary;
import com.dasd412.api.diaryservice.domain.diet.Diet;
import com.dasd412.api.diaryservice.domain.diet.EatTime;
import com.dasd412.api.diaryservice.domain.food.AmountUnit;
import com.dasd412.api.diaryservice.domain.food.Food;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.data.Offset;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SuppressWarnings("unused")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DiaryServiceApplication.class)
@TestPropertySource(locations = "/application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class DiaryUpdateRestControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UpdateDiaryService updateDiaryService;

    //todo JWT 도입 후 지울 듯?
    @MockBean
    private FindWriterFeignClient findWriterFeignClient;

    @MockBean
    KafkaSourceBean kafkaSourceBean;

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private DietRepository dietRepository;

    @Autowired
    private FoodRepository foodRepository;

    private MockMvc mockMvc;

    private static final String URL = "/diabetes-diary";

    @Before
    public void setUp() throws Exception {

        given(findWriterFeignClient.findWriterById(1L)).willReturn(1L);

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();

        // 단 1번만 포스트 쏘도록 제한.
        if(diaryRepository.findAll().size()==0){
            DiaryPostRequestDTO postRequestDTO = makePostRequestDto();

            mockMvc.perform(MockMvcRequestBuilders.post(URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(postRequestDTO))).andDo(print());
        }
    }

    private DiaryPostRequestDTO makePostRequestDto() {
        List<FoodPostRequestDTO> foodList1 = new ArrayList<>();
        foodList1.add(new FoodPostRequestDTO("toast", 50.0, AmountUnit.g));
        foodList1.add(new FoodPostRequestDTO("chicken", 150.0, AmountUnit.g));

        List<FoodPostRequestDTO> foodList2 = new ArrayList<>();
        foodList2.add(new FoodPostRequestDTO("coke", 100.0, AmountUnit.mL));

        List<DietPostRequestDTO> validDietList = new ArrayList<>();
        validDietList.add(new DietPostRequestDTO(EatTime.LUNCH, 150, foodList1));
        validDietList.add(new DietPostRequestDTO(EatTime.ELSE, 100, foodList2));

        return DiaryPostRequestDTO.builder().writerId(1L).fastingPlasmaGlucose(100).remark("test")
                .year("2021").month("12").day("22").hour("00").minute("00").second("00")
                .dietList(validDietList).build();
    }

    private ResultActions updateDTO(DiaryUpdateRequestDTO dto) throws Exception {
        return mockMvc.perform(put(URL).contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(dto)));
    }

    //일지
    @Test
    public void updateDiaryWhichHasInvalidFastingPlasmaGlucose() throws Exception {
        DiaryUpdateRequestDTO dto = DiaryUpdateRequestDTO.builder().writerId(1L).diaryId(1L)
                .fastingPlasmaGlucose(-100).remark("test").build();

        updateDTO(dto)
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.error.status").value("400"))
                .andExpect(jsonPath("$.error.message").value("java.lang.IllegalArgumentException"));
    }

    @Test
    public void updateDiaryWhichHasInvalidRemarkLength() throws Exception {
        StringBuilder sb = new StringBuilder();
        IntStream.range(0, 600).forEach(sb::append);

        DiaryUpdateRequestDTO dto = DiaryUpdateRequestDTO.builder().writerId(1L).diaryId(1L)
                .fastingPlasmaGlucose(100).remark(sb.toString()).build();

        updateDTO(dto)
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.error.status").value("400"))
                .andExpect(jsonPath("$.error.message").value("java.lang.IllegalArgumentException"));
    }

    @Test
    public void updateDiaryValid() throws Exception {
        DiaryUpdateRequestDTO dto = DiaryUpdateRequestDTO.builder().writerId(1L).diaryId(1L)
                .fastingPlasmaGlucose(200).remark("test1").build();

        updateDTO(dto)
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response.id").value("1"));

        DiabetesDiary found = diaryRepository.findAll().get(0);

        assertThat(found.getFastingPlasmaGlucose()).isEqualTo(200);
        assertThat(found.getRemark()).isEqualTo("test1");
    }

    //식단
    @Test
    public void updateDiaryWhichHasInvalidBloodSugar() throws Exception {

        DietUpdateRequestDTO dietDto = new DietUpdateRequestDTO(1L, EatTime.LUNCH, -100, null);

        List<DietUpdateRequestDTO> dietList = new ArrayList<>();
        dietList.add(dietDto);

        DiaryUpdateRequestDTO dto = DiaryUpdateRequestDTO.builder().writerId(1L).diaryId(1L)
                .fastingPlasmaGlucose(200).remark("test1").dietList(dietList).build();

        updateDTO(dto)
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.error.status").value("400"))
                .andExpect(jsonPath("$.error.message").value("java.lang.IllegalArgumentException"));
    }

    @Test
    public void updateDiaryWhichHasValidDiets() throws Exception {
        DietUpdateRequestDTO dietDto = new DietUpdateRequestDTO(1L, EatTime.BREAK_FAST, 120, null);

        List<DietUpdateRequestDTO> dietList = new ArrayList<>();
        dietList.add(dietDto);

        DiaryUpdateRequestDTO dto = DiaryUpdateRequestDTO.builder().writerId(1L).diaryId(1L)
                .fastingPlasmaGlucose(200).remark("test1").dietList(dietList).build();

        updateDTO(dto)
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response.id").value("1"));

        Diet found = dietRepository.findAll().get(0);

        assertThat(found.getEatTime()).isEqualTo(EatTime.BREAK_FAST);
        assertThat(found.getBloodSugar()).isEqualTo(120);
    }

    //음식
    @Test
    public void updateDiaryWhichHasInvalidFoodNameLength() throws Exception {
        FoodUpdateRequestDTO foodDto = new FoodUpdateRequestDTO(1L, "", 50.0, AmountUnit.NONE);

        List<FoodUpdateRequestDTO> foodList = new ArrayList<>();
        foodList.add(foodDto);

        DietUpdateRequestDTO dietDto = new DietUpdateRequestDTO(1L, EatTime.BREAK_FAST, 120, foodList);

        List<DietUpdateRequestDTO> dietList = new ArrayList<>();
        dietList.add(dietDto);

        DiaryUpdateRequestDTO dto = DiaryUpdateRequestDTO.builder().writerId(1L).diaryId(1L)
                .fastingPlasmaGlucose(200).remark("test1").dietList(dietList).build();

        updateDTO(dto)
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.error.status").value("400"))
                .andExpect(jsonPath("$.error.message").value("java.lang.IllegalArgumentException"));
    }

    @Test
    public void updateDiaryWhichHasInvalidFoodAmount() throws Exception {
        FoodUpdateRequestDTO foodDto = new FoodUpdateRequestDTO(1L, "toast", -100.0, AmountUnit.NONE);

        List<FoodUpdateRequestDTO> foodList = new ArrayList<>();
        foodList.add(foodDto);

        DietUpdateRequestDTO dietDto = new DietUpdateRequestDTO(1L, EatTime.BREAK_FAST, 120, foodList);

        List<DietUpdateRequestDTO> dietList = new ArrayList<>();
        dietList.add(dietDto);

        DiaryUpdateRequestDTO dto = DiaryUpdateRequestDTO.builder().writerId(1L).diaryId(1L)
                .fastingPlasmaGlucose(200).remark("test1").dietList(dietList).build();

        updateDTO(dto)
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.error.status").value("400"))
                .andExpect(jsonPath("$.error.message").value("java.lang.IllegalArgumentException"));
    }

    @Test
    public void updateDiaryWhichHasValidFoods() throws Exception {
        FoodUpdateRequestDTO foodDto = new FoodUpdateRequestDTO(2L, "coke", 100.0, AmountUnit.mL);

        List<FoodUpdateRequestDTO> foodList = new ArrayList<>();
        foodList.add(foodDto);

        DietUpdateRequestDTO dietDto = new DietUpdateRequestDTO(1L, EatTime.BREAK_FAST, 120, foodList);

        List<DietUpdateRequestDTO> dietList = new ArrayList<>();
        dietList.add(dietDto);

        DiaryUpdateRequestDTO dto = DiaryUpdateRequestDTO.builder().writerId(1L).diaryId(1L)
                .fastingPlasmaGlucose(200).remark("test1").dietList(dietList).build();

        updateDTO(dto)
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response.id").value("1"));

        Food found = foodRepository.findById(2L).orElseThrow(NoResultException::new);

        assertThat(found.getFoodName()).isEqualTo("coke");
        assertThat(found.getAmount()).isCloseTo(100.0, Offset.offset(0.05));
        assertThat(found.getAmountUnit()).isEqualTo(AmountUnit.mL);
    }
}
