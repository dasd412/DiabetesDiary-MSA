package com.dasd412.api.diaryservice.controller;


import com.dasd412.api.diaryservice.DiaryServiceApplication;
import com.dasd412.api.diaryservice.controller.dto.SecurityDiaryPostRequestDTO;
import com.dasd412.api.diaryservice.controller.dto.SecurityFoodDTO;
import com.dasd412.api.diaryservice.domain.diary.DiabetesDiary;
import com.dasd412.api.diaryservice.domain.diary.DiaryRepository;

import com.dasd412.api.diaryservice.service.client.FindWriterFeignClient;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DiaryServiceApplication.class)
@Execution(ExecutionMode.SAME_THREAD)
@TestPropertySource(locations = "/application-test.properties")
public class SaveDiaryResilienceTest {

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private DiaryRepository diaryRepository;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    //todo JWT 도입 후 지울 듯?
    @MockBean
    private FindWriterFeignClient findWriterFeignClient;

    private MockMvc mockMvc;

    private SecurityDiaryPostRequestDTO dto;

    private final String url = "/diabetes-diary";

    @Before
    public void setUpDTO() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();

        dto = makeDtoValid();
    }

    private SecurityDiaryPostRequestDTO makeDtoValid() {
        return SecurityDiaryPostRequestDTO.builder().writerId(1L).fastingPlasmaGlucose(100).remark("test")
                .year("2021").month("12").day("22").hour("00").minute("00").second("00")
                .build();
    }

    @After
    public void clean() {
        diaryRepository.deleteAll();
    }

    @Test
    public void testSaveDiaryWhenCircuitBreakClosedAndMicroServiceCallTimeOut() throws Exception {
        //given
        circuitBreakerRegistry.circuitBreaker("diaryService")
                .transitionToClosedState();

        //when
        when(findWriterFeignClient.findWriterById(1L)).thenThrow(new TimeoutException());
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.error.message").exists())
                .andExpect(jsonPath("$.error.status").value("500"));

        //then
        assertThat(diaryRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    public void testSaveDiaryWhenCircuitBreakClosedAndDataBaseTimeOut() throws Exception {
        //given
        circuitBreakerRegistry.circuitBreaker("diaryService")
                .transitionToClosedState();
        given(findWriterFeignClient.findWriterById(1L)).willReturn(1L);
        given(diaryRepository.save(any(DiabetesDiary.class))).willAnswer(invocation -> {
            throw new TimeoutException();
        });

        //when
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.error.message").exists())
                .andExpect(jsonPath("$.error.status").value("500"));

        //then
        assertThat(diaryRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    public void testSaveDiaryWhenCircuitBreakOpen() throws Exception {
        //given
        circuitBreakerRegistry.circuitBreaker("diaryService")
                .transitionToOpenState();

        given(findWriterFeignClient.findWriterById(1L)).willReturn(1L);
        given(diaryRepository.save(any(DiabetesDiary.class))).willAnswer(invocation -> {
            throw new TimeoutException();
        });

        //when
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.error.message").exists())
                .andExpect(jsonPath("$.error.status").value("500"));

        //then
        assertThat(diaryRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    public void testSaveDiaryWhenCircuitBreakClosedAndAllServiceAvailable() throws Exception {
        //given
        circuitBreakerRegistry.circuitBreaker("diaryService")
                .transitionToClosedState();

        given(findWriterFeignClient.findWriterById(1L)).willReturn(1L);

        //when
        when(diaryRepository.save(any(DiabetesDiary.class))).thenReturn(new DiabetesDiary());

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"));

        //then
        verify(diaryRepository).save(any());
    }
}
