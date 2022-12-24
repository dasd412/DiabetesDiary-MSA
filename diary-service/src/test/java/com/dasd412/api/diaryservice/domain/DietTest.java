package com.dasd412.api.diaryservice.domain;

import com.dasd412.api.diaryservice.config.JPATestConfiguration;
import com.dasd412.api.diaryservice.domain.diary.DiabetesDiary;
import com.dasd412.api.diaryservice.domain.diet.Diet;
import com.dasd412.api.diaryservice.adapter.out.persistence.diary.DiaryRepository;
import com.dasd412.api.diaryservice.adapter.out.persistence.diet.DietRepository;

import com.dasd412.api.diaryservice.domain.diet.EatTime;
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

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@Import({JPATestConfiguration.class})
@DataJpaTest()
@TestPropertySource(locations = "/application-test.properties")
public class DietTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private DietRepository dietRepository;

    private DiabetesDiary diary;

    @Before
    public void setUp(){
        diary = DiabetesDiary.builder()
                .writerId(1L)
                .fastingPlasmaGlucose(100)
                .writtenTime(LocalDateTime.now())
                .remark("test")
                .build();
    }

    @Test
    public void createDietWithMinusBloodSugar(){
        thrown.expect(IllegalArgumentException.class);

        Diet diet=Diet.builder()
                .diary(diary)
                .bloodSugar(-1)
                .eatTime(EatTime.ELSE)
                .build();
    }

    @Test
    public void createDietWithTooMuchBloodSugar(){
        thrown.expect(IllegalArgumentException.class);

        Diet diet=Diet.builder()
                .diary(diary)
                .bloodSugar(1001)
                .eatTime(EatTime.ELSE)
                .build();
    }

    @Test
    public void createValidDiet(){
        createValidDietEntity();

        DiabetesDiary found=diaryRepository.findAll().get(0);
        assertThat(dietRepository.findAll().size()).isEqualTo(1);
        assertThat(found.getDietList().get(0).getBloodSugar()).isEqualTo(120);
        assertThat(found.getDietList().get(0).getEatTime()).isEqualTo(EatTime.ELSE);
    }

    @Test
    public void updateMinusBloodSugar(){
        thrown.expect(IllegalArgumentException.class);

        createValidDietEntity();

        Diet found=dietRepository.findAll().get(0);
        found.modifyBloodSugar(-1);
    }

    @Test
    public void updateTooMuchBloodSugar(){
        thrown.expect(IllegalArgumentException.class);

        createValidDietEntity();

        Diet found=dietRepository.findAll().get(0);
        found.modifyBloodSugar(1002);
    }

    @Test
    public void update(){
        createValidDietEntity();

        Diet found=dietRepository.findAll().get(0);
        found.update(EatTime.BREAK_FAST,110);

        diaryRepository.save(diary);

        DiabetesDiary foundDiary=diaryRepository.findAll().get(0);
        assertThat(foundDiary.getDietList().get(0).getBloodSugar()).isEqualTo(110);
        assertThat(foundDiary.getDietList().get(0).getEatTime()).isEqualTo(EatTime.BREAK_FAST);
    }

    private void createValidDietEntity(){
        Diet diet= Diet.builder()
                .diary(diary)
                .bloodSugar(120)
                .eatTime(EatTime.ELSE)
                .build();

        diary.addDiet(diet);
        diaryRepository.save(diary);
    }
}
