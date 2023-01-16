package com.dasd412.api.readdiaryservice.common.config;

import com.dasd412.api.readdiaryservice.adapter.in.msessage.DiaryChannels;
import com.dasd412.api.readdiaryservice.adapter.in.msessage.WriterChannels;
import com.dasd412.api.readdiaryservice.adapter.in.msessage.handler.DiaryChangeHandler;
import com.dasd412.api.readdiaryservice.adapter.in.msessage.handler.WriterChangeHandler;
import com.dasd412.api.readdiaryservice.application.service.DiaryDataSyncService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableBinding({DiaryChannels.class, WriterChannels.class})
@ConditionalOnProperty(value = "kafka.enabled", matchIfMissing = true)
@Configuration
public class KafkaSinkConfiguration {

    private final DiaryChannels diaryChannels;

    private final WriterChannels writerChannels;

    private final DiaryDataSyncService diaryDataSyncService;

    public KafkaSinkConfiguration(DiaryChannels diaryChannels, WriterChannels writerChannels, DiaryDataSyncService diaryDataSyncService) {
        this.diaryChannels = diaryChannels;
        this.writerChannels = writerChannels;
        this.diaryDataSyncService = diaryDataSyncService;
    }

    @Bean
    public WriterChangeHandler writerChangeHandler() {
        return new WriterChangeHandler(diaryDataSyncService);
    }

    @Bean
    public DiaryChangeHandler diaryChangeHandler() {
        return new DiaryChangeHandler(diaryDataSyncService);
    }
}
