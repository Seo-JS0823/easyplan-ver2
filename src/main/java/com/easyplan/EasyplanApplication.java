package com.easyplan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EasyplanApplication {

	public static void main(String[] args) {
		SpringApplication.run(EasyplanApplication.class, args);
	}

}
