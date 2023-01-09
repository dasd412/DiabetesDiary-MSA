package com.dasd412.api.diaryservice.common.config;

import com.dasd412.api.diaryservice.adapter.in.message.WriterChannels;
import com.dasd412.api.diaryservice.adapter.in.message.handler.WriterChangeHandler;


import com.dasd412.api.diaryservice.application.service.DeleteDiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SuppressWarnings("unused")
@EnableBinding(WriterChannels.class)
@ConditionalOnProperty(value = "kafka.enabled", matchIfMissing = true)
@Configuration
public class KafkaSinkConfiguration {

    @Autowired
    private WriterChannels writerChannels;

    private final DeleteDiaryService deleteDiaryService;

    public KafkaSinkConfiguration(DeleteDiaryService deleteDiaryService) {
        this.deleteDiaryService = deleteDiaryService;
    }

    @Bean
    public WriterChangeHandler writerChangeHandler() {
        return new WriterChangeHandler(deleteDiaryService);
    }
}
