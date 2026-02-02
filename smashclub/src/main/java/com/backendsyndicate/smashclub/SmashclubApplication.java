package com.backendsyndicate.smashclub;

import com.backendsyndicate.smashclub.common.init.Init;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SmashclubApplication {

	public static void main(String[] args) {
		// Init settings here
        Init.load();

        SpringApplication.run(SmashclubApplication.class, args);
	}

}
