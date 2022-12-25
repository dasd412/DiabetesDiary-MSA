package com.dasd412.api.diaryservice.adapter.in.web;

import com.dasd412.api.diaryservice.DiaryServiceApplication;
import com.dasd412.api.diaryservice.adapter.in.web.dto.post.DiaryPostRequestDTO;
import com.dasd412.api.diaryservice.adapter.in.web.dto.post.DietDTO;
import com.dasd412.api.diaryservice.adapter.in.web.dto.post.FoodDTO;
import com.dasd412.api.diaryservice.adapter.out.persistence.diary.DiaryRepository;
import com.dasd412.api.diaryservice.adapter.out.persistence.diet.DietRepository;
import com.dasd412.api.diaryservice.domain.diet.EatTime;
import com.dasd412.api.diaryservice.domain.food.AmountUnit;
import com.dasd412.api.diaryservice.adapter.out.persistence.food.FoodRepository;
import com.dasd412.api.diaryservice.adapter.out.message.source.KafkaSourceBean;
import com.dasd412.api.diaryservice.application.service.impl.SaveDiaryServiceImpl;
import com.dasd412.api.diaryservice.adapter.out.client.FindWriterFeignClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = DiaryServiceApplication.class)
@TestPropertySource(locations = "/application-test.properties")
public class SaveDiaryControllerTest {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private SaveDiaryServiceImpl saveDiaryService;
    @MockBean
    KafkaSourceBean kafkaSourceBean;

    //todo JWT 도입 후 지울 듯?
    @MockBean
    private FindWriterFeignClient findWriterFeignClient;

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private DietRepository dietRepository;

    @Autowired
    private FoodRepository foodRepository;

    private MockMvc mockMvc;

    @Before
    public void setUpDTO() throws TimeoutException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();

        given(findWriterFeignClient.findWriterById(1L)).willReturn(1L);
    }

    @After
    public void clean() {
        diaryRepository.deleteAll();
        dietRepository.deleteAll();
        foodRepository.deleteAll();
    }

    private ResultActions postDto(DiaryPostRequestDTO dto) throws Exception {
        String url = "/diabetes-diary";
        return mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(dto)));
    }

    /*
    일지만 테스트
     */
    @Test
    public void postDiaryWhichHasInvalidFastingPlasmaGlucose() throws Exception {
        DiaryPostRequestDTO dto = makeDtoWhichHasInvalidFastingPlasmaGlucose();

        postDto(dto)
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.error.status").value("400"))
                .andExpect(jsonPath("$.error.message").value("java.lang.IllegalArgumentException"));
    }

    private DiaryPostRequestDTO makeDtoWhichHasInvalidFastingPlasmaGlucose() {
        return DiaryPostRequestDTO.builder().writerId(1L).fastingPlasmaGlucose(-1).remark("test")
                .year("2021").month("12").day("22").hour("00").minute("00").second("00").build();
    }

    @Test
    public void postDiaryWhichHasInvalidRemarkLength() throws Exception {
        DiaryPostRequestDTO dto = makeDtoWhichHasInvalidRemarkLength();

        postDto(dto)
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.error.status").value("400"))
                .andExpect(jsonPath("$.error.message").value("java.lang.IllegalArgumentException"));
    }

    private DiaryPostRequestDTO makeDtoWhichHasInvalidRemarkLength() {

        StringBuilder sb = new StringBuilder();
        IntStream.range(0, 600).forEach(sb::append);

        return DiaryPostRequestDTO.builder().writerId(1L).fastingPlasmaGlucose(100).remark(sb.toString())
                .year("2021").month("12").day("22").hour("00").minute("00").second("00").build();
    }

    @Test
    public void postDiaryWhichHasInvalidWriterId() throws Exception {
        DiaryPostRequestDTO dto = makeDtoWhichHasInvalidWriterId();

        postDto(dto)
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.error.status").value("400"))
                .andExpect(jsonPath("$.error.message").value("java.lang.IllegalArgumentException"));
    }

    private DiaryPostRequestDTO makeDtoWhichHasInvalidWriterId() {
        return DiaryPostRequestDTO.builder().writerId(-1L).fastingPlasmaGlucose(100).remark("test")
                .year("2021").month("12").day("22").hour("00").minute("00").second("00").build();
    }

    @Test
    public void postDiaryValid() throws Exception {
        DiaryPostRequestDTO dto = makeDtoWhichHasValidDiary();

        postDto(dto)
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response.id").value("1"));
    }

    private DiaryPostRequestDTO makeDtoWhichHasValidDiary() {
        return DiaryPostRequestDTO.builder().writerId(1L).fastingPlasmaGlucose(100).remark("test")
                .year("2021").month("12").day("22").hour("00").minute("00").second("00").build();
    }

    /*
    식단 테스트
     */
    @Test
    public void postDiaryWhichHasInvalidBloodSugar() throws Exception {
        DiaryPostRequestDTO dto = makeDtoWhichHasInvalidBloodSugar();

        postDto(dto)
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.error.status").value("400"))
                .andExpect(jsonPath("$.error.message").value("java.lang.IllegalArgumentException"));
    }

    private DiaryPostRequestDTO makeDtoWhichHasInvalidBloodSugar() {
        List<DietDTO> invalidDietList = new ArrayList<>();
        invalidDietList.add(new DietDTO(EatTime.LUNCH, 100, new ArrayList<>()));
        invalidDietList.add(new DietDTO(EatTime.ELSE, -1, new ArrayList<>()));

        return DiaryPostRequestDTO.builder().writerId(1L).fastingPlasmaGlucose(100).remark("test")
                .year("2021").month("12").day("22").hour("00").minute("00").second("00")
                .dietList(invalidDietList).build();
    }

    @Test
    public void postDiaryWhichHasValidDiets() throws Exception {
        DiaryPostRequestDTO dto = makeDtoWhichHasValidDiet();

        postDto(dto)
                .andExpect(jsonPath("$.success").value("true"));

        assertThat(diaryRepository.findAll().size()).isEqualTo(1);
        assertThat(dietRepository.findAll().size()).isEqualTo(4);
    }

    private DiaryPostRequestDTO makeDtoWhichHasValidDiet() {
        List<DietDTO> validDietList = new ArrayList<>();
        validDietList.add(new DietDTO(EatTime.LUNCH, 150, new ArrayList<>()));
        validDietList.add(new DietDTO(EatTime.ELSE, 100, new ArrayList<>()));
        validDietList.add(new DietDTO(EatTime.DINNER, 120, new ArrayList<>()));
        validDietList.add(new DietDTO(EatTime.ELSE, 100, new ArrayList<>()));

        return DiaryPostRequestDTO.builder().writerId(1L).fastingPlasmaGlucose(100).remark("test")
                .year("2021").month("12").day("22").hour("00").minute("00").second("00")
                .dietList(validDietList).build();
    }

    /*
    음식 테스트
     */

    @Test
    public void postDiaryWhichHasInvalidFoodNameLength() throws Exception {
        DiaryPostRequestDTO dto = makeDtoWhichHasInvalidFoodNameLength();

        postDto(dto)
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.error.status").value("400"))
                .andExpect(jsonPath("$.error.message").value("java.lang.IllegalArgumentException"));

    }

    private DiaryPostRequestDTO makeDtoWhichHasInvalidFoodNameLength() {
        List<FoodDTO> invalidFoodList = new ArrayList<>();
        invalidFoodList.add(new FoodDTO("", 50.0, AmountUnit.g));

        List<DietDTO> dietList = new ArrayList<>();
        dietList.add(new DietDTO(EatTime.LUNCH, 150, invalidFoodList));

        return DiaryPostRequestDTO.builder().writerId(1L).fastingPlasmaGlucose(100).remark("test")
                .year("2021").month("12").day("22").hour("00").minute("00").second("00")
                .dietList(dietList).build();
    }

    @Test
    public void postDiaryWhichHasInvalidFoodAmount() throws Exception {
        DiaryPostRequestDTO dto = makeDtoWhichHasInvalidFoodAmount();

        postDto(dto)
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.error.status").value("400"))
                .andExpect(jsonPath("$.error.message").value("java.lang.IllegalArgumentException"));
    }

    private DiaryPostRequestDTO makeDtoWhichHasInvalidFoodAmount() {
        List<FoodDTO> invalidFoodList = new ArrayList<>();
        invalidFoodList.add(new FoodDTO("toast", -50.0, AmountUnit.g));

        List<DietDTO> dietList = new ArrayList<>();
        dietList.add(new DietDTO(EatTime.LUNCH, 150, invalidFoodList));

        return DiaryPostRequestDTO.builder().writerId(1L).fastingPlasmaGlucose(100).remark("test")
                .year("2021").month("12").day("22").hour("00").minute("00").second("00")
                .dietList(dietList).build();
    }

    @Test
    public void postDiaryWhichHasValidFoods() throws Exception {
        DiaryPostRequestDTO dto = makeDtoWhichHasValidFood();

        postDto(dto)
                .andExpect(jsonPath("$.success").value("true"));

        assertThat(diaryRepository.findAll().size()).isEqualTo(1);
        assertThat(dietRepository.findAll().size()).isEqualTo(2);
        assertThat(foodRepository.findAll().size()).isEqualTo(3);
    }

    private DiaryPostRequestDTO makeDtoWhichHasValidFood() {
        List<FoodDTO> foodList1 = new ArrayList<>();
        foodList1.add(new FoodDTO("toast", 50.0, AmountUnit.g));
        foodList1.add(new FoodDTO("chicken", 150.0, AmountUnit.g));

        List<FoodDTO> foodList2 = new ArrayList<>();
        foodList2.add(new FoodDTO("coke", 100.0, AmountUnit.mL));

        List<DietDTO> validDietList = new ArrayList<>();
        validDietList.add(new DietDTO(EatTime.LUNCH, 150, foodList1));
        validDietList.add(new DietDTO(EatTime.ELSE, 100, foodList2));

        return DiaryPostRequestDTO.builder().writerId(1L).fastingPlasmaGlucose(100).remark("test")
                .year("2021").month("12").day("22").hour("00").minute("00").second("00")
                .dietList(validDietList).build();
    }

}
