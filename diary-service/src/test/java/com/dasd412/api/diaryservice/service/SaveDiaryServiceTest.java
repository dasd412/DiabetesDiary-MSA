package com.dasd412.api.diaryservice.service;

import com.dasd412.api.diaryservice.DiaryServiceApplication;
import com.dasd412.api.diaryservice.controller.dto.SecurityDiaryPostRequestDTO;
import com.dasd412.api.diaryservice.domain.diary.DiabetesDiary;
import com.dasd412.api.diaryservice.domain.diary.DiaryRepository;
import com.dasd412.api.diaryservice.service.client.FindWriterFeignClient;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DiaryServiceApplication.class)
@Execution(ExecutionMode.SAME_THREAD)
public class SaveDiaryServiceTest {

    @Autowired
    private SaveDiaryService saveDiaryService;

    @MockBean
    private FindWriterFeignClient findWriterFeignClient;

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    private SecurityDiaryPostRequestDTO dto;


    @Before
    public void setUpDTO() {
        dto = new SecurityDiaryPostRequestDTO(1L, 100, "TEST");
    }

    @After
    public void clean() {
        diaryRepository.deleteAll();
    }

    @Test
    public void testSaveDiaryWhenCircuitBreakClosedAndServiceCallTimeOut() throws TimeoutException {
        circuitBreakerRegistry.circuitBreaker("diaryService")
                .transitionToClosedState();

        when(findWriterFeignClient.findWriterById(1L)).thenThrow(new TimeoutException());

        try {
            saveDiaryService.postDiaryWithEntities(dto);

            fail("이 블록까지 도달하면 테스트 실패!");

        } catch (Exception e) {
            assertThat(TimeoutException.class).isAssignableFrom(e.getClass());
            assertThat(diaryRepository.findAll().size()).isEqualTo(0);
        }
    }

    @Test
    public void testSaveDiaryWhenCircuitBreakOpen() throws TimeoutException {
        circuitBreakerRegistry.circuitBreaker("diaryService")
                .transitionToOpenState();

        when(findWriterFeignClient.findWriterById(1L)).thenThrow(new TimeoutException());

        try {
            saveDiaryService.postDiaryWithEntities(dto);

            fail("이 블록까지 도달하면 테스트 실패!");

        } catch (Exception e) {
            assertThat(TimeoutException.class).isAssignableFrom(e.getClass());
            assertThat(diaryRepository.findAll().size()).isEqualTo(0);
        }
    }

    @Test
    public void testSaveDiaryWhenCircuitBreakClosedAndServiceAvailable() throws TimeoutException {
        circuitBreakerRegistry.circuitBreaker("diaryService")
                .transitionToClosedState();

        given(findWriterFeignClient.findWriterById(1L)).willReturn(1L);

        saveDiaryService.postDiaryWithEntities(dto);

        DiabetesDiary savedLatest = diaryRepository.findAll().get(0);

        assertThat(savedLatest.getFastingPlasmaGlucose()).isEqualTo(dto.getFastingPlasmaGlucose());
        assertThat(savedLatest.getRemark()).isEqualTo(dto.getRemark());
    }
}