package com.dasd412.api.writerservice.domain.writer;

import com.dasd412.api.writerservice.adapter.out.persistence.writer.WriterRepository;
import com.dasd412.api.writerservice.config.JPATestConfiguration;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@Import({JPATestConfiguration.class})
@DataJpaTest()
@TestPropertySource(locations = "/application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class WriterTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Autowired
    private WriterRepository writerRepository;

    @After
    public void clean() {
        writerRepository.deleteAll();
    }

    @Test
    public void createWithNullEmail() {
        thrown.expect(IllegalArgumentException.class);

        Writer writer = Writer.builder().name("test").email(null).password("test").build();
        writerRepository.save(writer);
    }

    @Test
    public void createWithInvalidEmail() {
        thrown.expect(IllegalArgumentException.class);

        Writer writer = Writer.builder().name("test").email("test").password("test").build();
        writerRepository.save(writer);
    }

    @Test
    public void createWithValidEmail() {
        Writer writer = Writer.builder().name("test").email("test@test.com").password("test").build();
        writerRepository.save(writer);

        assertThat(writerRepository.findAll().size()).isEqualTo(1);
        assertThat(writerRepository.findAll().get(0).getEmail()).isEqualTo(writer.getEmail());
    }

    @Test
    public void modifyEmailToNull() {
        thrown.expect(IllegalArgumentException.class);
        Writer writer = Writer.builder().name("test").email("test@test.com").password("test").build();

        writer.modifyEmail(null);
    }

    @Test
    public void modifyEmailToInvalidFormat() {
        thrown.expect(IllegalArgumentException.class);
        Writer writer = Writer.builder().name("test").email("test@test.com").password("test").build();

        writer.modifyEmail("test");
    }

    @Test
    public void modifyEmailToValidFormat() {
        Writer writer = Writer.builder().name("test").email("test@test.com").password("test").build();
        writer.modifyEmail("valid@valid.net");

        assertThat(writer.getEmail()).isEqualTo("valid@valid.net");
    }
}