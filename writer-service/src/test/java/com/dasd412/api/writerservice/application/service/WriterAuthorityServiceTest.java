package com.dasd412.api.writerservice.application.service;

import com.dasd412.api.writerservice.adapter.out.persistence.authority.AuthorityRepository;
import com.dasd412.api.writerservice.adapter.out.persistence.writer.WriterRepository;
import com.dasd412.api.writerservice.adapter.out.persistence.writer_authority.WriterAuthorityRepository;
import com.dasd412.api.writerservice.application.service.writerauthority.WriterAuthorityService;

import com.dasd412.api.writerservice.application.service.writerauthority.impl.WriterAuthorityServiceImpl;
import com.dasd412.api.writerservice.config.JPATestConfiguration;
import com.dasd412.api.writerservice.domain.authority.Authority;
import com.dasd412.api.writerservice.domain.authority.Role;
import com.dasd412.api.writerservice.domain.writer.Writer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@Import({JPATestConfiguration.class})
@DataJpaTest
@TestPropertySource(locations = "/application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class WriterAuthorityServiceTest {

    private WriterAuthorityService writerAuthorityService;

    @Autowired
    private WriterRepository writerRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private WriterAuthorityRepository writerAuthorityRepository;

    @Before
    public void setUp() {
        this.writerAuthorityService = new WriterAuthorityServiceImpl(writerRepository, authorityRepository, writerAuthorityRepository);
    }

    @Test
    public void createWriterAuthorityTriple() throws TimeoutException {
        //given
        writerRepository.save(new Writer("test", "test@com", "test", "test", "test"));
        authorityRepository.save(new Authority(Role.USER));
        authorityRepository.save(new Authority(Role.PATIENT));
        authorityRepository.save(new Authority(Role.ENTERPRISE));

        //when
        writerAuthorityService.createWriterAuthority(1L, 1L);
        writerAuthorityService.createWriterAuthority(1L, 2L);
        writerAuthorityService.createWriterAuthority(1L, 3L);

        //then
        assertThat(writerAuthorityRepository.findAll().size()).isEqualTo(3);
    }

    @Test
    public void findAllAuthorityOfWriter() throws TimeoutException {
        //given
        writerRepository.save(new Writer("test", "test@com", "test", "test", "test"));
        authorityRepository.save(new Authority(Role.USER));
        authorityRepository.save(new Authority(Role.PATIENT));
        authorityRepository.save(new Authority(Role.ENTERPRISE));

        //when
        writerAuthorityService.createWriterAuthority(1L, 1L);
        writerAuthorityService.createWriterAuthority(1L, 2L);
        writerAuthorityService.createWriterAuthority(1L, 3L);

        //then
        List<Authority> authorityList = writerAuthorityService.findAllAuthority(1L);
        assertThat(authorityList.size()).isEqualTo(3);
        assertThat(authorityList.get(0).getRole()).isEqualTo(Role.USER);
        assertThat(authorityList.get(1).getRole()).isEqualTo(Role.PATIENT);
        assertThat(authorityList.get(2).getRole()).isEqualTo(Role.ENTERPRISE);
    }
}
