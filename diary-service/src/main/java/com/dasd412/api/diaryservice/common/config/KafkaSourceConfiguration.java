package com.dasd412.api.diaryservice.common.config;

import com.dasd412.api.diaryservice.adapter.out.message.DiaryChannels;
import com.dasd412.api.diaryservice.adapter.out.message.source.KafkaSourceBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableBinding(DiaryChannels.class)
@ConditionalOnProperty(value = "kafka.enabled", matchIfMissing = true)
@Configuration
public class KafkaSourceConfiguration {

    @Autowired
    private DiaryChannels diaryChannels;

    @Bean
    public KafkaSourceBean kafkaSourceBean() {
        return new KafkaSourceBean(diaryChannels);
    }
}
