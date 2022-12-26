package com.dasd412.api.diaryservice.adapter.in.web;

import com.dasd412.api.diaryservice.DiaryServiceApplication;
import com.dasd412.api.diaryservice.adapter.in.web.dto.delete.DiaryDeleteRequestDTO;
import com.dasd412.api.diaryservice.adapter.in.web.dto.post.DiaryPostRequestDTO;
import com.dasd412.api.diaryservice.adapter.in.web.dto.post.DietPostRequestDTO;
import com.dasd412.api.diaryservice.adapter.in.web.dto.post.FoodPostRequestDTO;
import com.dasd412.api.diaryservice.adapter.out.client.FindWriterFeignClient;
import com.dasd412.api.diaryservice.adapter.out.message.source.KafkaSourceBean;
import com.dasd412.api.diaryservice.adapter.out.persistence.diary.DiaryRepository;
import com.dasd412.api.diaryservice.adapter.out.persistence.diet.DietRepository;
import com.dasd412.api.diaryservice.adapter.out.persistence.food.FoodRepository;
import com.dasd412.api.diaryservice.domain.diet.EatTime;
import com.dasd412.api.diaryservice.domain.food.AmountUnit;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DiaryServiceApplication.class)
@TestPropertySource(locations = "/application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class DiaryDeleteRestControllerTest {

    @Autowired
    private WebApplicationContext context;

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

    private static final String URL = "/diabetes-diary";

    @Before
    public void setUp() throws Exception {
        if (mockMvc == null) {
            mockMvc = MockMvcBuilders
                    .webAppContextSetup(context)
                    .build();
        }
        given(findWriterFeignClient.findWriterById(1L)).willReturn(1L);

        // 단 1번만 포스트 쏘도록 제한.
        if (diaryRepository.findAll().size() == 0) {
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

    @Test
    public void deleteDiary() throws Exception {
        mockMvc.perform(delete(URL).contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(new DiaryDeleteRequestDTO(1L, 1L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response.id").value("1"));

        assertThat(diaryRepository.findAll().size()).isEqualTo(0);
        assertThat(dietRepository.findAll().size()).isEqualTo(0);
        assertThat(foodRepository.findAll().size()).isEqualTo(0);
    }
}
