package com.easyplan._global.initializer;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.easyplan.finance.application.required.AccountOptionRepository;
import com.easyplan.finance.domain.account.AccountOption;
import com.easyplan.finance.domain.account.AccountOptionTemplate;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Order(1)
public class DataInitializer implements CommandLineRunner {
	
	private final AccountOptionRepository accountOptionRepo;
	
	@Override
	public void run(String... args) throws Exception {
		if(accountOptionRepo.count() == 0) {
			List<AccountOption> defaultOptions = AccountOptionTemplate.defaultOptions();
			
			accountOptionRepo.saveAll(defaultOptions);
		}
	}
}
