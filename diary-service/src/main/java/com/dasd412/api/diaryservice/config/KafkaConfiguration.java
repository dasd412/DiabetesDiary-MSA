package com.dasd412.api.diaryservice.config;

import com.dasd412.api.diaryservice.message.source.KafkaSourceBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableBinding(Source.class)
@ConditionalOnProperty(value = "kafka.enabled", matchIfMissing = true)
@Configuration
public class KafkaConfiguration {

    @Autowired
    private Source source;

    @Bean
    public KafkaSourceBean kafkaSourceBean() {
        return new KafkaSourceBean(source);
    }
}
