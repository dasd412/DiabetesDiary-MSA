package com.dasd412.api.readdiaryservice.adapter.out.persistence;

import com.dasd412.api.readdiaryservice.adapter.in.web.FoodPageVO;
import com.dasd412.api.readdiaryservice.adapter.out.persistence.diary.DiaryDocumentRepository;
import com.dasd412.api.readdiaryservice.adapter.out.web.dto.FoodBoardDTO;
import com.dasd412.api.readdiaryservice.application.service.ReadDiaryService;
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
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "/application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FoodBoardPagingTest {

    @Autowired
    private ReadDiaryService readDiaryService;

    @Autowired
    private DiaryDocumentRepository diaryDocumentRepository;

    private long dietId = 1;

    private long foodId = 1;

    @Before
    public void setup() {
        this.dietId = 1;

        this.foodId = 1;

        IntStream.range(0, 20).forEach(
                i -> {
                    saveDiaryDocument((long) (i + 1), i + 1);
                }
        );
    }

    //일지 1개에 식단 3개, 식단 1개당 음식 3개. 따라서 일지 1개는 음식 9개를 갖는다.
    private void saveDiaryDocument(Long diaryId, int day) {

        List<DietDocument> dietDocuments = makeDietDocumentList(diaryId, day);

        DiabetesDiaryDocument diaryDocument = DiabetesDiaryDocument.builder()
                .diaryId(diaryId)
                .writerId(1L)
                .fastingPlasmaGlucose(100)
                .remark("test")
                .writtenTime(LocalDateTime.of(2023, 1, day, 0, 0))
                .dietDocuments(dietDocuments).build();

        diaryDocumentRepository.save(diaryDocument);
    }

    private List<DietDocument> makeDietDocumentList(Long diaryId, int someValue) {
        return Arrays.asList(
                makeDietDocument(diaryId, EatTime.BREAK_FAST, someValue),
                makeDietDocument(diaryId, EatTime.LUNCH, someValue),
                makeDietDocument(diaryId, EatTime.DINNER, someValue)
        );
    }

    private DietDocument makeDietDocument(Long diaryId, EatTime eatTime, int someValue) {
        List<FoodDocument> meal = makeFoodDocumentList(dietId, someValue);

        DietDocument dietDocument = DietDocument.builder()
                .dietId(dietId).diaryId(diaryId).writerId(1L)
                .eatTime(eatTime).bloodSugar(100 + someValue)
                .foodList(meal).build();

        dietId += 1;

        return dietDocument;
    }

    private List<FoodDocument> makeFoodDocumentList(Long dietId, int someValue) {
        return Arrays.asList(
                makeFoodDocument(dietId, someValue),
                makeFoodDocument(dietId, someValue),
                makeFoodDocument(dietId, someValue)
        );
    }

    private FoodDocument makeFoodDocument(Long dietId, int someValue) {
        FoodDocument food = FoodDocument.builder()
                .foodId(foodId).dietId(dietId).writerId(1L)
                .foodName(String.valueOf(foodId)).amount(someValue).amountUnit(AmountUnit.g).build();

        foodId += 1;

        return food;
    }

    @After
    public void clean() {
        this.diaryDocumentRepository.deleteAll();
    }

    @Test
    public void testDefaultPaging() {
        FoodPageVO vo = new FoodPageVO();
        Page<FoodBoardDTO> dtoPage = readDiaryService.getFoodByPagination("1", vo);

        assertThat(dtoPage.getTotalPages()).isEqualTo(20);
    }
}
