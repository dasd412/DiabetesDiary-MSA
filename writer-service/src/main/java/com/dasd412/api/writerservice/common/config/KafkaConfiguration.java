package com.dasd412.api.writerservice.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(value = "kafka.enabled", matchIfMissing = true)
@Configuration
public class KafkaConfiguration {
    /*
    카프카 관련은 @Component로 지정하지 말고 여기에 @Bean으로 따로 등록할 것.
    안 그러면 단위 테스트할 때 카프카 들어감.
     */
}
