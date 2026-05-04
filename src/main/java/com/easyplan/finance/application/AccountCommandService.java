package com.easyplan.finance.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.application.provided.account.AccountCommand;
import com.easyplan.finance.application.provided.account.AccountFinder;
import com.easyplan.finance.application.provided.account.AccountPolicy;
import com.easyplan.finance.application.provided.ledger.LedgerFinder;
import com.easyplan.finance.application.provided.ledger.LedgerPolicy;
import com.easyplan.finance.application.required.AccountCategoryRepository;
import com.easyplan.finance.application.required.AccountOptionRepository;
import com.easyplan.finance.application.required.AccountRepository;
import com.easyplan.finance.domain.account.Account;
import com.easyplan.finance.domain.account.AccountOption;
import com.easyplan.finance.domain.account.request.AccountCreateRequest;
import com.easyplan.finance.domain.account.request.AccountUpdateRequest.AccountInfoUpdate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountCommandService implements AccountCommand {
	private final LedgerFinder ledgerFinder;
	
	private final AccountFinder accountFinder;
	
	private final AccountCategoryRepository accountCategoryRepo;
	
	private final AccountOptionRepository accountOptionRepo;
	
	private final AccountRepository accountRepo;
	
	private final AccountPolicy accountPolicy;
	
	private final LedgerPolicy ledgerPolicy;

	@Override
	public Account createAccount(PublicId memberPublicId, PublicId ledgerPublicId, AccountCreateRequest accountCreate) {
		ledgerPolicy.validateForLedgerOwnership(memberPublicId, ledgerPublicId);
		
		Account account = accountPolicy.validateForCreateAccount(ledgerPublicId, accountCreate);
		return accountRepo.save(account);
	}

	@Override
	public Account updateAccountInfo(PublicId memberPublicId, PublicId accountPublicId, AccountInfoUpdate accountInfo) {
		Account account = accountPolicy.validateForUpdateAccount(memberPublicId, accountPublicId, accountInfo);
		
		AccountOption option = accountOptionRepo.findByOptionCode(accountInfo.optionCode());
		
		account.changeAccountInfo(option.getId(), accountInfo);
		
		return accountRepo.save(account);
	}
	
}
