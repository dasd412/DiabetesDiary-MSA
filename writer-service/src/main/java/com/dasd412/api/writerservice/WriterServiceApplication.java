package com.dasd412.api.writerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@RefreshScope
@SpringBootApplication
public class WriterServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WriterServiceApplication.class, args);
	}

}
