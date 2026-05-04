package com.easyplan.finance.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.application.provided.account.AccountFinder;
import com.easyplan.finance.application.required.AccountCategoryRepository;
import com.easyplan.finance.application.required.AccountRepository;
import com.easyplan.finance.domain.account.Account;
import com.easyplan.finance.domain.account.AccountCategory;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountFindService implements AccountFinder {

	private final AccountRepository accountRepo;

	private final AccountCategoryRepository accountCategoryRepo;
	
	@Override
	public List<Account> findByLedgerId(Long ledgerId) {
		return accountRepo.findByLedgerId(ledgerId);
	}

	@Override
	public List<AccountCategory> findCategoryByLedgerId(Long ledgerId) {
		return accountCategoryRepo.findByLedgerId(ledgerId);
	}

	@Override
	public boolean existsByCategoryIdAndAccountName(Long categoryId, String accountName) {
		return accountRepo.existsByCategoryIdAndAccountName(categoryId, accountName);
	}

	@Override
	public Account findByAccountPublicId(PublicId accountPublicId) {
		return accountRepo.findByAccountPublicId(accountPublicId);
	}

	@Override
	public boolean existsByLedgerIdAndAccountPublicId(Long ledgerId, PublicId accountPublicId) {
		return accountRepo.existsByLedgerIdAndAccountPublicId(ledgerId, accountPublicId);
	}
	
	
}
