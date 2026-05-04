package com.easyplan.finance.application;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.application.dto.AccountDetail;
import com.easyplan.finance.application.provided.account.AccountFinder;
import com.easyplan.finance.application.provided.account.AccountPolicy;
import com.easyplan.finance.application.provided.ledger.LedgerPolicy;
import com.easyplan.finance.application.required.AccountCategoryRepository;
import com.easyplan.finance.application.required.AccountRepository;
import com.easyplan.finance.domain.account.Account;
import com.easyplan.finance.domain.account.AccountCategory;
import com.easyplan.finance.domain.account.AccountStatus;
import com.easyplan.finance.domain.account.AccountType;
import com.easyplan.finance.domain.account.exception.AccountException;
import com.easyplan.finance.domain.account.exception.AccountExceptionCode;
import com.easyplan.finance.domain.ledger.Ledger;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountFindService implements AccountFinder {

	private final AccountRepository accountRepo;

	private final AccountCategoryRepository accountCategoryRepo;
	
	private final LedgerPolicy ledgerPolicy;
	
	private final AccountPolicy accountPolicy;
	
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

	@Override
	public Map<AccountType, List<Account>> findByLedgerPublicId(PublicId memberPublicId, PublicId ledgerPublicId) {
		Ledger ledger = ledgerPolicy.validateForLedgerOwnership(memberPublicId, ledgerPublicId);
		
		List<Account> accounts = accountRepo.findByLedgerIdAndStatus(ledger.getId(), AccountStatus.ACTIVE);
		
		List<AccountCategory> categories = accountCategoryRepo.findByLedgerId(ledger.getId());
		
		Map<Long, AccountType> accountTypeMap = categories.stream()
				.collect(Collectors.toMap(
						AccountCategory::getId,
						AccountCategory::getAccountType
				));
		
		Map<AccountType, List<Account>> accountMap = accounts.stream()
				.collect(Collectors.groupingBy(acc -> accountTypeMap.get(acc.getCategoryId())));
		
		return accountMap;
	}

	@Override
	public AccountDetail findOneByAccountPublicId(PublicId memberPublicId, PublicId accountPublicId) {
		Account account = accountPolicy.validateForAccountOwnership(memberPublicId, accountPublicId);
		
		AccountType accountType = accountCategoryRepo.findById(account.getCategoryId())
				.orElseThrow(() -> new AccountException(AccountExceptionCode.ACCOUNT_NOT_FOUND))
				.getAccountType();
		
		return new AccountDetail(
				accountType,
				account.getAccountPublicId().publicId(),
				account.getAccountName(),
				account.getAccountDescription()
		);
	}

	@Override
	public Map<AccountType, List<Account>> findByLedgerIdAndDeactivate(PublicId memberPublicId, PublicId ledgerPublicId) {
		Ledger ledger = ledgerPolicy.validateForLedgerOwnership(memberPublicId, ledgerPublicId);
		
		List<Account> deactivateAccounts = accountRepo.findByLedgerIdAndStatus(ledger.getId(), AccountStatus.DEACTIVATE);
		
		List<AccountCategory> categories = accountCategoryRepo.findByLedgerId(ledger.getId());
		
		Map<Long, AccountType> accountTypeMap = categories.stream()
				.collect(Collectors.toMap(
						AccountCategory::getId,
						AccountCategory::getAccountType
				));
		
		Map<AccountType, List<Account>> accountMap = deactivateAccounts.stream()
				.collect(Collectors.groupingBy(acc -> accountTypeMap.get(acc.getCategoryId())));
		
		return accountMap;
	}
}
