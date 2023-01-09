package com.dasd412.api.diaryservice.application.service.impl;

import com.dasd412.api.diaryservice.DiaryServiceApplication;
import com.dasd412.api.diaryservice.adapter.in.web.dto.post.DiaryPostRequestDTO;
import com.dasd412.api.diaryservice.adapter.in.web.dto.post.DietPostRequestDTO;
import com.dasd412.api.diaryservice.adapter.in.web.dto.post.FoodPostRequestDTO;
import com.dasd412.api.diaryservice.adapter.out.message.source.KafkaSourceBean;
import com.dasd412.api.diaryservice.adapter.out.persistence.diary.DiaryRepository;
import com.dasd412.api.diaryservice.adapter.out.persistence.diet.DietRepository;
import com.dasd412.api.diaryservice.adapter.out.persistence.food.FoodRepository;
import com.dasd412.api.diaryservice.application.service.DeleteDiaryService;
import com.dasd412.api.diaryservice.application.service.SaveDiaryService;

import com.dasd412.api.diaryservice.domain.diet.EatTime;
import com.dasd412.api.diaryservice.domain.food.AmountUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DiaryServiceApplication.class)
@TestPropertySource(locations = "/application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class DeleteDiaryServiceImplTest {

    @Autowired
    private SaveDiaryService saveDiaryService;

    @Autowired
    private DeleteDiaryService deleteDiaryService;

    @MockBean
    private KafkaSourceBean kafkaSourceBean;

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private DietRepository dietRepository;

    @Autowired
    private FoodRepository foodRepository;

    @Before
    public void setUp() throws TimeoutException {
        DiaryPostRequestDTO dto = makePostRequestDto();
        saveDiaryService.postDiaryWithEntities(1L, dto);
    }

    private DiaryPostRequestDTO makePostRequestDto() {
        List<FoodPostRequestDTO> foodList1 = new ArrayList<>();
        foodList1.add(new FoodPostRequestDTO("toast", 50.0, AmountUnit.g));
        foodList1.add(new FoodPostRequestDTO("chicken", 150.0, AmountUnit.g));

        List<FoodPostRequestDTO> foodList2 = new ArrayList<>();
        foodList2.add(new FoodPostRequestDTO("coke", 100.0, AmountUnit.mL));

        List<FoodPostRequestDTO> foodList3 = new ArrayList<>();
        foodList3.add(new FoodPostRequestDTO("toast", 50.0, AmountUnit.g));
        foodList3.add(new FoodPostRequestDTO("chicken", 150.0, AmountUnit.g));

        List<FoodPostRequestDTO> foodList4 = new ArrayList<>();
        foodList4.add(new FoodPostRequestDTO("toast", 50.0, AmountUnit.g));
        foodList4.add(new FoodPostRequestDTO("coke", 50.0, AmountUnit.mL));

        List<DietPostRequestDTO> validDietList = new ArrayList<>();
        validDietList.add(new DietPostRequestDTO(EatTime.BREAK_FAST, 100, foodList1));
        validDietList.add(new DietPostRequestDTO(EatTime.LUNCH, 150, foodList2));
        validDietList.add(new DietPostRequestDTO(EatTime.DINNER, 110, foodList3));
        validDietList.add(new DietPostRequestDTO(EatTime.ELSE, 130, foodList4));


        return DiaryPostRequestDTO.builder().fastingPlasmaGlucose(100).remark("test")
                .year("2021").month("12").day("22").hour("00").minute("00").second("00")
                .dietList(validDietList).build();
    }

    @Test
    public void deleteAllOfWriter() throws TimeoutException {
        //when
        deleteDiaryService.deleteAllOfWriter(1L);

        //then
        assertThat(diaryRepository.findAll().size()).isEqualTo(0);
        assertThat(dietRepository.findAll().size()).isEqualTo(0);
        assertThat(foodRepository.findAll().size()).isEqualTo(0);
    }
}