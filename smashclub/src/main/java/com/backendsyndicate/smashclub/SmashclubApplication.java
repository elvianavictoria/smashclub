package com.backendsyndicate.smashclub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SmashclubApplication {

	public static void main(String[] args) {
        SpringApplication.run(SmashclubApplication.class, args);
	}

}
