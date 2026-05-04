package com.easyplan.finance.application.required;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easyplan.finance.domain.account.AccountOption;

public interface AccountOptionRepository extends JpaRepository<AccountOption, Long> {
	
}
