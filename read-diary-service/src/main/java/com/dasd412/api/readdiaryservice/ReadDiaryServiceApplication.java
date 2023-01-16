package com.dasd412.api.readdiaryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@SpringBootApplication
public class ReadDiaryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReadDiaryServiceApplication.class, args);
	}

}
