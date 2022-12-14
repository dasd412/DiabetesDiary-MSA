package com.dasd412.api.diaryservice.domain;

import com.dasd412.api.diaryservice.config.JPATestConfiguration;
import com.dasd412.api.diaryservice.domain.diary.DiabetesDiary;
import com.dasd412.api.diaryservice.domain.diary.DiaryRepository;

import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@RunWith(SpringRunner.class)
@Import({JPATestConfiguration.class})
@DataJpaTest()
@TestPropertySource(locations = "/application-test.properties")
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class DiabetesDiaryTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private DiabetesDiary diary;

    @Autowired
    private DiaryRepository repository;

    @Test
    public void createInvalidFastingPlasmaGlucoseWithMinusValue() {
        thrown.expect(IllegalArgumentException.class);

        diary = DiabetesDiary.builder()
                .writerId(1L)
                .fastingPlasmaGlucose(-1)
                .remark("test")
                .writtenTime(LocalDateTime.now())
                .build();
    }

    @Test
    public void createInvalidFastingPlasmaGlucoseWithTooMuchValue() {
        thrown.expect(IllegalArgumentException.class);

        diary = DiabetesDiary.builder()
                .writerId(1L)
                .fastingPlasmaGlucose(1001)
                .remark("test")
                .writtenTime(LocalDateTime.now())
                .build();
    }

    @Test
    public void createInvalidRemarkLength() {
        thrown.expect(IllegalArgumentException.class);

        StringBuilder remark = new StringBuilder();
        IntStream.range(0, 501).forEach(i -> remark.append("a"));

        diary = DiabetesDiary.builder()
                .writerId(1L)
                .fastingPlasmaGlucose(100)
                .remark(remark.toString())
                .writtenTime(LocalDateTime.now())
                .build();
    }

    @Test
    public void createWithInvalidWriterId() {
        thrown.expect(IllegalArgumentException.class);

        diary = DiabetesDiary.builder()
                .writerId(-1L)
                .fastingPlasmaGlucose(100)
                .remark("test")
                .writtenTime(LocalDateTime.now())
                .build();
    }

    @Test
    public void createWithNullWriterId() {
        thrown.expect(IllegalArgumentException.class);

        diary = DiabetesDiary.builder()
                .writerId(null)
                .fastingPlasmaGlucose(100)
                .writtenTime(LocalDateTime.now())
                .remark("test")
                .build();
    }

    @Test
    public void saveValidDiaryEntity() {
        diary = DiabetesDiary.builder()
                .writerId(1L)
                .fastingPlasmaGlucose(100)
                .writtenTime(LocalDateTime.now())
                .remark("test")
                .build();

        repository.save(diary);

        DiabetesDiary found = repository.findAll().get(0);

        assertThat(repository.findAll().size()).isEqualTo(1);
        assertThat(found.getWriterId()).isEqualTo(diary.getWriterId());
        assertThat(found.getRemark()).isEqualTo(diary.getRemark());
        assertThat(found.getFastingPlasmaGlucose()).isEqualTo(diary.getFastingPlasmaGlucose());
    }

    @Test
    public void updateDiaryWithInvalidFastingPlasmaGlucose() {
        thrown.expect(IllegalArgumentException.class);

        DiabetesDiary valid = makeValidDiaryForUpdate();

        valid.modifyFastingPlasmaGlucose(-1);
    }

    @Test
    public void updateDiaryWithInvalidRemarkLength() {
        thrown.expect(IllegalArgumentException.class);

        DiabetesDiary valid = makeValidDiaryForUpdate();

        StringBuilder remark = new StringBuilder();
        IntStream.range(0, 501).forEach(i -> remark.append("a"));

        valid.modifyRemark(remark.toString());
    }

    @Test
    public void updateDiary() {
        DiabetesDiary valid = makeValidDiaryForUpdate();
        repository.save(valid);

        valid.update(110, "update");
        repository.save(valid);

        DiabetesDiary found = repository.findAll().get(0);

        assertThat(found.getFastingPlasmaGlucose()).isEqualTo(110);
        assertThat(found.getRemark()).isEqualTo("update");
    }

    private DiabetesDiary makeValidDiaryForUpdate() {
        return DiabetesDiary.builder()
                .writerId(1L)
                .fastingPlasmaGlucose(100)
                .writtenTime(LocalDateTime.now())
                .remark("test")
                .build();
    }
}
