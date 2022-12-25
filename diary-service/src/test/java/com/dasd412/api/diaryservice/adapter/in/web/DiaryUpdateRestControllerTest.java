package com.dasd412.api.diaryservice.adapter.in.web;

import com.dasd412.api.diaryservice.DiaryServiceApplication;
import com.dasd412.api.diaryservice.adapter.in.web.dto.post.DiaryPostRequestDTO;
import com.dasd412.api.diaryservice.adapter.in.web.dto.post.DietPostRequestDTO;
import com.dasd412.api.diaryservice.adapter.in.web.dto.post.FoodPostRequestDTO;
import com.dasd412.api.diaryservice.adapter.out.client.FindWriterFeignClient;
import com.dasd412.api.diaryservice.adapter.out.message.source.KafkaSourceBean;
import com.dasd412.api.diaryservice.adapter.out.persistence.diary.DiaryRepository;
import com.dasd412.api.diaryservice.adapter.out.persistence.diet.DietRepository;
import com.dasd412.api.diaryservice.adapter.out.persistence.food.FoodRepository;
import com.dasd412.api.diaryservice.application.service.UpdateDiaryService;
import com.dasd412.api.diaryservice.domain.diet.EatTime;
import com.dasd412.api.diaryservice.domain.food.AmountUnit;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DiaryServiceApplication.class)
@TestPropertySource(locations = "/application-test.properties")
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
        if (mockMvc==null){
            mockMvc = MockMvcBuilders
                    .webAppContextSetup(context)
                    .build();
        }

        given(findWriterFeignClient.findWriterById(1L)).willReturn(1L);

        DiaryPostRequestDTO dto = makePostRequestDto();

        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)));
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

    @After
    public void clean() {
        diaryRepository.deleteAll();
        dietRepository.deleteAll();
        foodRepository.deleteAll();
    }
    //일지

    @Test
    public void updateDiaryWhichHasInvalidFastingPlasmaGlucose() throws Exception {

    }

    @Test
    public void updateDiaryWhichHasInvalidRemarkLength() throws Exception {

    }

    @Test
    public void updateDiaryWhichHasInvalidWriterId() throws Exception {

    }

    @Test
    public void updateDiaryValid() throws Exception {

    }

    //식단
    @Test
    public void updateDiaryWhichHasInvalidBloodSugar() throws Exception {

    }

    @Test
    public void updateDiaryWhichHasValidDiets() throws Exception {

    }

    //음식
    @Test
    public void updateDiaryWhichHasInvalidFoodNameLength() throws Exception {

    }

    @Test
    public void updateDiaryWhichHasInvalidFoodAmount() throws Exception {

    }

    @Test
    public void updateDiaryWhichHasBlankFoodUnit() throws Exception {

    }

    @Test
    public void updateDiaryWhichHasValidFoods() throws Exception {

    }
}
