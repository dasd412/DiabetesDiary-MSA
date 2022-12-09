package com.dasd412.api.writerservice.config;

import com.dasd412.api.writerservice.message.model.DiaryChangeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(value = "kafka.enabled", matchIfMissing = true)
@EnableBinding(Sink.class)
@Configuration
public class KafkaConfiguration {
    /*
    카프카 관련은 @Component로 지정하지 말고 여기에 @Bean으로 따로 등록할 것.
    안 그러면 단위 테스트할 때 카프카 들어감.
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @StreamListener(Sink.INPUT)
    public void logSinkMessage(DiaryChangeModel diaryChange) {
        logger.debug("Received an {} event for writer id {} and diary id {}", diaryChange.getAction(), diaryChange.getWriterId(), diaryChange.getDiaryId());
    }
}
