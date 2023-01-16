package com.dasd412.api.writerservice.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@ConditionalOnProperty(value = "redis.enabled", matchIfMissing = true)
@Configuration
public class RedisConfiguration {

    @Value("${redis.server}")
    private String hostName;

    @Value("${redis.port}")
    private String redisPort;

    //레디스 서버 커넥션 설정
    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(hostName, Integer.parseInt(redisPort));
        return new JedisConnectionFactory(configuration);
    }

    //레디스 서버에 액션을 실행할 redis template 생성
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }
}
