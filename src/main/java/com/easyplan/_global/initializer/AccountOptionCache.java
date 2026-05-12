package com.easyplan._global.initializer;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.easyplan.finance.application.required.repository.AccountOptionRepository;
import com.easyplan.finance.domain.account.AccountOption;
import com.easyplan.finance.domain.account.AccountOptionTemplate;
import com.easyplan.finance.domain.account.exception.AccountErrorCode;
import com.easyplan.finance.domain.account.exception.AccountException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Order(2)
public class AccountOptionCache implements CommandLineRunner {
	
	private final AccountOptionRepository accountOptionRepo;
	
	private final Map<AccountOptionTemplate, Long> optionMap = new EnumMap<>(AccountOptionTemplate.class);
	
	@Override
	public void run(String... args) throws Exception {
		optionMap.clear();
		
		optionMap.putAll(
				accountOptionRepo.findAll().stream()
						.collect(Collectors.toMap(AccountOption::getOptionCode, AccountOption::getId))
		);
	}
	
	public Long getId(AccountOptionTemplate option) {
    Long optionId = optionMap.get(option);

    if (optionId == null) {
        throw new AccountException(AccountErrorCode.ACCOUNT_OPTION_MAPPING_ERROR);
    }

    return optionId;
}
}
