package com.dasd412.api.diaryservice.domain;

import com.dasd412.api.diaryservice.config.JPATestConfiguration;
import com.dasd412.api.diaryservice.domain.diary.DiabetesDiary;
import com.dasd412.api.diaryservice.domain.diet.Diet;
import com.dasd412.api.diaryservice.adapter.out.persistence.diary.DiaryRepository;
import com.dasd412.api.diaryservice.adapter.out.persistence.diet.DietRepository;
import com.dasd412.api.diaryservice.domain.diet.EatTime;
import com.dasd412.api.diaryservice.domain.food.AmountUnit;
import com.dasd412.api.diaryservice.domain.food.Food;
import com.dasd412.api.diaryservice.adapter.out.persistence.food.FoodRepository;
import org.assertj.core.data.Offset;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@Import({JPATestConfiguration.class})
@DataJpaTest()
@TestPropertySource(locations = "/application-test.properties")
public class FoodTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private DietRepository dietRepository;

    @Autowired
    private FoodRepository foodRepository;

    private DiabetesDiary diary;

    private Diet diet;

    @Before
    public void setUp() {
        diary = DiabetesDiary.builder()
                .writerId(1L)
                .fastingPlasmaGlucose(100)
                .writtenTime(LocalDateTime.now())
                .remark("test")
                .build();

        diet = Diet.builder()
                .diary(diary)
                .bloodSugar(120)
                .eatTime(EatTime.ELSE)
                .build();

        diary.addDiet(diet);
    }

    @Test
    public void createFoodWithZeroLengthFoodName() {
        thrown.expect(IllegalArgumentException.class);

        Food food = Food.builder()
                .foodName("")
                .amount(11)
                .amountUnit(AmountUnit.g)
                .build();
    }

    @Test
    public void createFoodWithTooLongFoodName() {
        thrown.expect(IllegalArgumentException.class);

        StringBuilder sb = new StringBuilder();
        IntStream.range(0, StringMaxLength.FOOD_NAME + 1).forEach(sb::append);

        Food food = Food.builder()
                .foodName(sb.toString())
                .amount(11)
                .amountUnit(AmountUnit.g)
                .build();
    }

    @Test
    public void createFoodWithMinusAmount() {
        thrown.expect(IllegalArgumentException.class);

        Food food = Food.builder()
                .foodName("salt")
                .amount(-1)
                .amountUnit(AmountUnit.g)
                .build();
    }

    @Test
    public void createValidFood() {
        createValidFoodEntity();

        Food found = foodRepository.findAll().get(0);
        assertThat(found.getFoodName()).isEqualTo("salt");
        assertThat(found.getAmount()).isCloseTo(5.50, Offset.offset(0.1));
        assertThat(found.getAmountUnit()).isEqualTo(AmountUnit.g);
    }

    @Test
    public void updateZeroLengthFoodName() {
        thrown.expect(IllegalArgumentException.class);

        createValidFoodEntity();

        Food created = foodRepository.findAll().get(0);
        created.modifyFoodName("");
    }

    @Test
    public void updateTooLongFoodName() {
        thrown.expect(IllegalArgumentException.class);

        createValidFoodEntity();

        Food created = foodRepository.findAll().get(0);

        StringBuilder sb = new StringBuilder();
        IntStream.range(0, StringMaxLength.FOOD_NAME + 1).forEach(sb::append);

        created.modifyFoodName(sb.toString());
    }

    @Test
    public void updateMinusAmount() {
        thrown.expect(IllegalArgumentException.class);

        createValidFoodEntity();
        Food created = foodRepository.findAll().get(0);

        created.modifyAmount(-1.0);
    }

    @Test
    public void update() {
        createValidFoodEntity();

        Food created = foodRepository.findAll().get(0);
        created.update("coke", 100.0, AmountUnit.mL);

        diaryRepository.save(diary);

        Food found = foodRepository.findAll().get(0);
        assertThat(found.getFoodName()).isEqualTo("coke");
        assertThat(found.getAmount()).isCloseTo(100.0, Offset.offset(0.1));
        assertThat(found.getAmountUnit()).isEqualTo(AmountUnit.mL);
    }

    private void createValidFoodEntity() {
        Food food = Food.builder()
                .diet(diet)
                .foodName("salt")
                .amount(5.50)
                .amountUnit(AmountUnit.g)
                .build();

        diet.addFood(food);
        diaryRepository.save(diary);
    }
}
