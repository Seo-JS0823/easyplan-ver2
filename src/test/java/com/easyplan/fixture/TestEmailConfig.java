package com.easyplan.fixture;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class TestEmailConfig {
	
	@Bean
	@Primary
	FakeEmailSender fakeEmailSender() {
		return new FakeEmailSender(); 
	}
}
