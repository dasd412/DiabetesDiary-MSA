package com.dasd412.api.writerservice.common.config;

import com.dasd412.api.writerservice.adapter.out.message.WriterChannels;
import com.dasd412.api.writerservice.adapter.out.message.source.KafkaSourceBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableBinding(WriterChannels.class)
@ConditionalOnProperty(value = "kafka.enabled", matchIfMissing = true)
@Configuration
public class KafkaSourceConfiguration {

    @Autowired
    private WriterChannels writerChannels;

    @Bean
    public KafkaSourceBean kafkaSourceForWithdrawal() {
        return new KafkaSourceBean(writerChannels);
    }
}
