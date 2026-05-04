package com.easyplan.finance.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easyplan.finance.application.provided.account.AccountCommand;
import com.easyplan.finance.application.provided.account.AccountFinder;
import com.easyplan.finance.application.required.AccountRepository;
import com.easyplan.finance.domain.account.Account;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountCommandService implements AccountCommand {
	private final AccountFinder accountFinder;
	
	private final AccountRepository accountRepo;

	@Override
	public Account createAccount() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Account updateAccountInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Account deactivate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Account reactivate() {
		// TODO Auto-generated method stub
		return null;
	}
	
	//private final AccountPolicy accountPolicy;
	
	
}
