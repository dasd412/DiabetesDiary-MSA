package com.dasd412.api.diaryservice.controller;


import com.dasd412.api.diaryservice.DiaryServiceApplication;
import com.dasd412.api.diaryservice.controller.dto.SecurityDiaryPostRequestDTO;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DiaryServiceApplication.class)
@Execution(ExecutionMode.SAME_THREAD)
public class SaveDiaryControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @MockBean
    private FindWriterFeignClient findWriterFeignClient;

    private MockMvc mockMvc;

    private SecurityDiaryPostRequestDTO dto;

    private final String url = "/api/diary/user/diabetes-diary";

    @Before
    public void setUpDTO() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();

        dto = new SecurityDiaryPostRequestDTO(1L, 100, "TEST");

    }

    @After
    public void clean() {
        diaryRepository.deleteAll();
    }

    @Test
    public void testSaveDiaryWhenCircuitBreakClosedAndServiceCallTimeOut() throws Exception {
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
    public void testSaveDiaryWhenCircuitBreakOpen() throws Exception {
        //given
        circuitBreakerRegistry.circuitBreaker("diaryService")
                .transitionToOpenState();

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
    public void testSaveDiaryWhenCircuitBreakClosedAndServiceAvailable() throws Exception {
        //given
        circuitBreakerRegistry.circuitBreaker("diaryService")
                .transitionToClosedState();

        given(findWriterFeignClient.findWriterById(1L)).willReturn(1L);

        //when
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response.id").value(1L));

        //then
        DiabetesDiary savedLatest = diaryRepository.findAll().get(0);

        assertThat(savedLatest.getFastingPlasmaGlucose()).isEqualTo(dto.getFastingPlasmaGlucose());
        assertThat(savedLatest.getRemark()).isEqualTo(dto.getRemark());
    }

}
