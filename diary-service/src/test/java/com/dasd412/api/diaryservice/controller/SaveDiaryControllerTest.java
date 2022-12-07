package com.dasd412.api.diaryservice.controller;

import com.dasd412.api.diaryservice.DiaryServiceApplication;
import com.dasd412.api.diaryservice.controller.dto.SecurityDiaryPostRequestDTO;
import com.dasd412.api.diaryservice.controller.dto.SecurityDietDTO;
import com.dasd412.api.diaryservice.controller.dto.SecurityFoodDTO;
import com.dasd412.api.diaryservice.domain.diary.DiaryRepository;
import com.dasd412.api.diaryservice.domain.diet.DietRepository;
import com.dasd412.api.diaryservice.domain.diet.EatTime;
import com.dasd412.api.diaryservice.domain.food.AmountUnit;
import com.dasd412.api.diaryservice.domain.food.FoodRepository;
import com.dasd412.api.diaryservice.service.client.FindWriterFeignClient;

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

    private ResultActions postDto(SecurityDiaryPostRequestDTO dto) throws Exception {
        String url = "/diabetes-diary";
        return mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(dto)));
    }

    /*
    일지만 테스트
     */
    @Test
    public void postDiaryWhichHasInvalidFastingPlasmaGlucose() throws Exception {
        SecurityDiaryPostRequestDTO dto = makeDtoWhichHasInvalidFastingPlasmaGlucose();

        postDto(dto)
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.error.status").value("400"))
                .andExpect(jsonPath("$.error.message").value("java.lang.IllegalArgumentException"));
    }

    private SecurityDiaryPostRequestDTO makeDtoWhichHasInvalidFastingPlasmaGlucose() {
        return SecurityDiaryPostRequestDTO.builder().writerId(1L).fastingPlasmaGlucose(-1).remark("test")
                .year("2021").month("12").day("22").hour("00").minute("00").second("00").build();
    }

    @Test
    public void postDiaryWhichHasInvalidRemarkLength() throws Exception {
        SecurityDiaryPostRequestDTO dto = makeDtoWhichHasInvalidRemarkLength();

        postDto(dto)
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.error.status").value("400"))
                .andExpect(jsonPath("$.error.message").value("java.lang.IllegalArgumentException"));
    }

    private SecurityDiaryPostRequestDTO makeDtoWhichHasInvalidRemarkLength() {

        StringBuilder sb = new StringBuilder();
        IntStream.range(0, 600).forEach(sb::append);

        return SecurityDiaryPostRequestDTO.builder().writerId(1L).fastingPlasmaGlucose(100).remark(sb.toString())
                .year("2021").month("12").day("22").hour("00").minute("00").second("00").build();
    }

    @Test
    public void postDiaryWhichHasInvalidWriterId() throws Exception {
        SecurityDiaryPostRequestDTO dto = makeDtoWhichHasInvalidWriterId();

        postDto(dto)
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.error.status").value("400"))
                .andExpect(jsonPath("$.error.message").value("java.lang.IllegalArgumentException"));
    }

    private SecurityDiaryPostRequestDTO makeDtoWhichHasInvalidWriterId() {
        return SecurityDiaryPostRequestDTO.builder().writerId(-1L).fastingPlasmaGlucose(100).remark("test")
                .year("2021").month("12").day("22").hour("00").minute("00").second("00").build();
    }

    @Test
    public void postDiaryValid() throws Exception {
        SecurityDiaryPostRequestDTO dto = makeDtoWhichHasValidDiary();

        postDto(dto)
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response.id").value("1"));
    }

    private SecurityDiaryPostRequestDTO makeDtoWhichHasValidDiary() {
        return SecurityDiaryPostRequestDTO.builder().writerId(1L).fastingPlasmaGlucose(100).remark("test")
                .year("2021").month("12").day("22").hour("00").minute("00").second("00").build();
    }

    /*
    식단 테스트
     */
    @Test
    public void postDiaryWhichHasInvalidBloodSugar() throws Exception {
        SecurityDiaryPostRequestDTO dto = makeDtoWhichHasInvalidBloodSugar();

        postDto(dto)
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.error.status").value("400"))
                .andExpect(jsonPath("$.error.message").value("java.lang.IllegalArgumentException"));
    }

    private SecurityDiaryPostRequestDTO makeDtoWhichHasInvalidBloodSugar() {
        List<SecurityDietDTO> invalidDietList = new ArrayList<>();
        invalidDietList.add(new SecurityDietDTO(EatTime.LUNCH, 100, new ArrayList<>()));
        invalidDietList.add(new SecurityDietDTO(EatTime.ELSE, -1, new ArrayList<>()));

        return SecurityDiaryPostRequestDTO.builder().writerId(1L).fastingPlasmaGlucose(100).remark("test")
                .year("2021").month("12").day("22").hour("00").minute("00").second("00")
                .dietList(invalidDietList).build();
    }

    @Test
    public void postDiaryWhichHasValidDiets() throws Exception {
        SecurityDiaryPostRequestDTO dto = makeDtoWhichHasValidDiet();

        postDto(dto)
                .andExpect(jsonPath("$.success").value("true"));

        assertThat(diaryRepository.findAll().size()).isEqualTo(1);
        assertThat(dietRepository.findAll().size()).isEqualTo(4);
    }

    private SecurityDiaryPostRequestDTO makeDtoWhichHasValidDiet() {
        List<SecurityDietDTO> validDietList = new ArrayList<>();
        validDietList.add(new SecurityDietDTO(EatTime.LUNCH, 150, new ArrayList<>()));
        validDietList.add(new SecurityDietDTO(EatTime.ELSE, 100, new ArrayList<>()));
        validDietList.add(new SecurityDietDTO(EatTime.DINNER, 120, new ArrayList<>()));
        validDietList.add(new SecurityDietDTO(EatTime.ELSE, 100, new ArrayList<>()));

        return SecurityDiaryPostRequestDTO.builder().writerId(1L).fastingPlasmaGlucose(100).remark("test")
                .year("2021").month("12").day("22").hour("00").minute("00").second("00")
                .dietList(validDietList).build();
    }

    /*
    음식 테스트
     */

    @Test
    public void postDiaryWhichHasInvalidFoodNameLength() throws Exception {
        SecurityDiaryPostRequestDTO dto = makeDtoWhichHasInvalidFoodNameLength();

        postDto(dto)
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.error.status").value("400"))
                .andExpect(jsonPath("$.error.message").value("java.lang.IllegalArgumentException"));

    }

    private SecurityDiaryPostRequestDTO makeDtoWhichHasInvalidFoodNameLength() {
        List<SecurityFoodDTO> invalidFoodList = new ArrayList<>();
        invalidFoodList.add(new SecurityFoodDTO("", 50.0, AmountUnit.g));

        List<SecurityDietDTO> dietList = new ArrayList<>();
        dietList.add(new SecurityDietDTO(EatTime.LUNCH, 150, invalidFoodList));

        return SecurityDiaryPostRequestDTO.builder().writerId(1L).fastingPlasmaGlucose(100).remark("test")
                .year("2021").month("12").day("22").hour("00").minute("00").second("00")
                .dietList(dietList).build();
    }

    @Test
    public void postDiaryWhichHasInvalidFoodAmount() throws Exception {
        SecurityDiaryPostRequestDTO dto = makeDtoWhichHasInvalidFoodAmount();

        postDto(dto)
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.error.status").value("400"))
                .andExpect(jsonPath("$.error.message").value("java.lang.IllegalArgumentException"));
    }

    private SecurityDiaryPostRequestDTO makeDtoWhichHasInvalidFoodAmount() {
        List<SecurityFoodDTO> invalidFoodList = new ArrayList<>();
        invalidFoodList.add(new SecurityFoodDTO("toast", -50.0, AmountUnit.g));

        List<SecurityDietDTO> dietList = new ArrayList<>();
        dietList.add(new SecurityDietDTO(EatTime.LUNCH, 150, invalidFoodList));

        return SecurityDiaryPostRequestDTO.builder().writerId(1L).fastingPlasmaGlucose(100).remark("test")
                .year("2021").month("12").day("22").hour("00").minute("00").second("00")
                .dietList(dietList).build();
    }

    @Test
    public void postDiaryWhichHasValidFoods() throws Exception {
        SecurityDiaryPostRequestDTO dto = makeDtoWhichHasValidFood();

        postDto(dto)
                .andExpect(jsonPath("$.success").value("true"));

        assertThat(diaryRepository.findAll().size()).isEqualTo(1);
        assertThat(dietRepository.findAll().size()).isEqualTo(2);
        assertThat(foodRepository.findAll().size()).isEqualTo(3);
    }

    private SecurityDiaryPostRequestDTO makeDtoWhichHasValidFood() {
        List<SecurityFoodDTO> foodList1 = new ArrayList<>();
        foodList1.add(new SecurityFoodDTO("toast", 50.0, AmountUnit.g));
        foodList1.add(new SecurityFoodDTO("chicken", 150.0, AmountUnit.g));

        List<SecurityFoodDTO> foodList2 = new ArrayList<>();
        foodList2.add(new SecurityFoodDTO("coke", 100.0, AmountUnit.mL));

        List<SecurityDietDTO> validDietList = new ArrayList<>();
        validDietList.add(new SecurityDietDTO(EatTime.LUNCH, 150, foodList1));
        validDietList.add(new SecurityDietDTO(EatTime.ELSE, 100, foodList2));

        return SecurityDiaryPostRequestDTO.builder().writerId(1L).fastingPlasmaGlucose(100).remark("test")
                .year("2021").month("12").day("22").hour("00").minute("00").second("00")
                .dietList(validDietList).build();
    }

}
