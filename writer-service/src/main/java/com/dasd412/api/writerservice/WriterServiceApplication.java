package com.dasd412.api.writerservice;

import com.dasd412.api.writerservice.message.model.DiaryChangeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@RefreshScope
@EnableJpaAuditing
@EnableBinding(Sink.class)
@SpringBootApplication
public class WriterServiceApplication {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static void main(String[] args) {
        SpringApplication.run(WriterServiceApplication.class, args);
    }
    @StreamListener(Sink.INPUT)
    public void logSinkMessage(DiaryChangeModel diaryChange) {
        logger.debug("Received an {} event for writer id {} and diary id {}", diaryChange.getAction(), diaryChange.getWriterId(), diaryChange.getDiaryId());
    }
}
