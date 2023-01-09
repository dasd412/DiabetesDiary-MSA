package com.dasd412.api.writerservice.common.config;

import com.dasd412.api.writerservice.adapter.in.message.DiaryChannels;
import com.dasd412.api.writerservice.adapter.in.message.handler.DiaryChangeHandler;
import com.dasd412.api.writerservice.adapter.out.message.source.KafkaSourceBean;
import com.dasd412.api.writerservice.adapter.out.message.WriterChannels;
import com.dasd412.api.writerservice.application.service.writer.UpdateWriterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableBinding({WriterChannels.class,DiaryChannels.class})
@ConditionalOnProperty(value = "kafka.enabled", matchIfMissing = true)
@Configuration
public class KafkaConfiguration {
    /*
    카프카 관련은 @Component로 지정하지 말고 여기에 @Bean으로 따로 등록할 것.
    안 그러면 단위 테스트할 때 카프카 들어감.
     */

    @Autowired
    private WriterChannels writerChannels;

    private final UpdateWriterService updateWriterService;

    public KafkaConfiguration(UpdateWriterService updateWriterService) {
        this.updateWriterService = updateWriterService;
    }

    @Bean
    public DiaryChangeHandler diaryChangeHandler() {
        return new DiaryChangeHandler(updateWriterService);
    }

    @Bean
    public KafkaSourceBean kafkaSourceForWithdrawal() {
        return new KafkaSourceBean(writerChannels);
    }

}
