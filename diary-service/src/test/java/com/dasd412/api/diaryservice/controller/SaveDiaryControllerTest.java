package com.dasd412.api.diaryservice.controller;

import com.dasd412.api.diaryservice.DiaryServiceApplication;
import com.dasd412.api.diaryservice.controller.dto.SecurityDiaryPostRequestDTO;
import com.dasd412.api.diaryservice.domain.diary.DiaryRepository;
import com.dasd412.api.diaryservice.domain.diet.DietRepository;
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

import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

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


}
