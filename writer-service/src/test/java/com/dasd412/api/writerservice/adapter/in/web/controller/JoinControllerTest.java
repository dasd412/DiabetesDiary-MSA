package com.dasd412.api.writerservice.adapter.in.web.controller;

import com.dasd412.api.writerservice.WriterServiceApplication;
import com.dasd412.api.writerservice.adapter.in.security.dto.UserJoinRequestDTO;
import com.dasd412.api.writerservice.domain.authority.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WriterServiceApplication.class)
@TestPropertySource(locations = "/application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class JoinControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private final String URL = "/signup";

    @Before
    public void setup() {
        if (mockMvc == null) {
            mockMvc = MockMvcBuilders
                    .webAppContextSetup(context)
                    .build();
        }
    }

    @Test
    public void invalidName() throws Exception {
        //given
        List<Role> rolesList = new ArrayList<>();
        rolesList.add(Role.TESTER);

        UserJoinRequestDTO dto = new UserJoinRequestDTO("", "before@naver.com", "empty", rolesList);
        //when and then
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void invalidEmail() throws Exception {
        //given
        List<Role> rolesList = new ArrayList<>();
        rolesList.add(Role.TESTER);

        UserJoinRequestDTO dto = new UserJoinRequestDTO("name", "sdasda", "pp", rolesList);
        //when and then
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void invalidPassword() throws Exception {
        //given
        List<Role> rolesList = new ArrayList<>();
        rolesList.add(Role.TESTER);

        UserJoinRequestDTO dto = new UserJoinRequestDTO("name", "before@naver.com", " ", rolesList);
        //when and then
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void invalidRoles() throws Exception {
        //given
        UserJoinRequestDTO dto = new UserJoinRequestDTO("name", "before@naver.com", "teasesatas", null);
        //when and then
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

}