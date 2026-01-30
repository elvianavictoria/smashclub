package com.backendsyndicate.smashclub;

import com.backendsyndicate.smashclub.payment.constant.ConstantLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SmashclubApplication {

	public static void main(String[] args) {
		// Init settings here
        ConstantLoader.load();

        SpringApplication.run(SmashclubApplication.class, args);
	}

}
