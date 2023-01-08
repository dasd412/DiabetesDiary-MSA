package com.dasd412.api.writerservice.adapter.in.web.controller;

import com.dasd412.api.writerservice.WriterServiceApplication;
import com.dasd412.api.writerservice.adapter.in.web.controller.dto.UserJoinRequestDTO;
import com.dasd412.api.writerservice.adapter.out.persistence.authority.AuthorityRepository;
import com.dasd412.api.writerservice.adapter.out.persistence.writer.WriterRepository;
import com.dasd412.api.writerservice.adapter.out.persistence.writer_authority.WriterAuthorityRepository;
import com.dasd412.api.writerservice.domain.authority.Authority;
import com.dasd412.api.writerservice.domain.authority.Role;
import com.dasd412.api.writerservice.domain.authority.WriterAuthority;
import com.dasd412.api.writerservice.domain.writer.Writer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.NoResultException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    @Autowired
    private WriterRepository writerRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private WriterAuthorityRepository writerAuthorityRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Before
    public void setup() {
        if (mockMvc == null) {
            mockMvc = MockMvcBuilders
                    .webAppContextSetup(context)
                    .build();
        }
    }

    @After
    public void clean() {
        writerAuthorityRepository.deleteAll();
        writerRepository.deleteAll();
        authorityRepository.deleteAll();
    }


    @Test
    public void invalidName() throws Exception {
        //given
        Set<Role> roles = new HashSet<>();
        roles.add(Role.TESTER);

        UserJoinRequestDTO dto = new UserJoinRequestDTO("", "before@naver.com", "empty", roles);
        //when and then
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void invalidEmail() throws Exception {
        //given
        Set<Role> roles = new HashSet<>();
        roles.add(Role.TESTER);

        UserJoinRequestDTO dto = new UserJoinRequestDTO("name", "sdasda", "pp", roles);
        //when and then
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void invalidPassword() throws Exception {
        //given
        Set<Role> roles = new HashSet<>();
        roles.add(Role.TESTER);

        UserJoinRequestDTO dto = new UserJoinRequestDTO("name", "before@naver.com", " ", roles);
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

    @Test
    public void joinWithOneAuthority() throws Exception {
        //given
        Set<Role> roles = new HashSet<>();
        roles.add(Role.TESTER);

        UserJoinRequestDTO dto = new UserJoinRequestDTO("tester", "before@naver.com", "teasesatas", roles);

        //when
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"));


        //then
        Writer writer = writerRepository.findWriterByName("tester").orElseThrow(NoResultException::new);

        assertThat(writer.getName()).isEqualTo(dto.getUsername());
        assertThat(writer.getEmail()).isEqualTo(dto.getEmail());
        assertThat(bCryptPasswordEncoder.matches(dto.getPassword(), writer.getPassword())).isTrue();

        List<Authority> authorities = authorityRepository.findAllAuthority(writer.getId());

        assertThat(authorities.size()).isEqualTo(1);
        assertThat(authorities.get(0).getRole()).isEqualTo(Role.TESTER);

        List<WriterAuthority>writerAuthorities=writerAuthorityRepository.findAllWriterAuthority(writer.getId());

        assertThat(writerAuthorities.size()).isEqualTo(1);
    }

    @Test
    public void joinWithSeveralAuthorities() throws Exception {
        //given
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        roles.add(Role.PATIENT);
        roles.add(Role.TESTER);

        UserJoinRequestDTO dto = new UserJoinRequestDTO("tester", "before@naver.com", "teasesatas", roles);

        //when
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"));


        //then
        Writer writer = writerRepository.findWriterByName("tester").orElseThrow(NoResultException::new);

        assertThat(writer.getName()).isEqualTo(dto.getUsername());
        assertThat(writer.getEmail()).isEqualTo(dto.getEmail());
        assertThat(bCryptPasswordEncoder.matches(dto.getPassword(), writer.getPassword())).isTrue();

        List<Authority> authorities = authorityRepository.findAllAuthority(writer.getId());

        assertThat(authorities.size()).isEqualTo(3);

        Set<Role> roleSet = new HashSet<>();
        authorities.forEach(elem -> roleSet.add(elem.getRole()));

        assertThat(roleSet.contains(Role.USER)).isTrue();
        assertThat(roleSet.contains(Role.PATIENT)).isTrue();
        assertThat(roleSet.contains(Role.TESTER)).isTrue();

        List<WriterAuthority>writerAuthorities=writerAuthorityRepository.findAllWriterAuthority(writer.getId());

        assertThat(writerAuthorities.size()).isEqualTo(3);
    }

    @Test
    public void alreadyExistUsername() throws Exception {
        //given
        writerRepository.save(Writer.builder().name("tester").email("test1@naver.com").build());

        //when and then
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);

        UserJoinRequestDTO dto = new UserJoinRequestDTO("tester", "test2@naver.com", "teasesatas", roles);
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(jsonPath("$.success").value("false"));
    }

    @Test
    public void alreadyExistEmail() throws Exception {
        //given
        writerRepository.save(Writer.builder().name("tester1").email("test@naver.com").build());

        //when and then
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);

        UserJoinRequestDTO dto = new UserJoinRequestDTO("tester2", "test@naver.com", "teasesatas", roles);
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(jsonPath("$.success").value("false"));
    }

}