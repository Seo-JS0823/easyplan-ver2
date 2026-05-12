package com.easyplan.finance.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.easyplan._global.initializer.AccountOptionCache;
import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.application.policy.AccountPolicy;
import com.easyplan.finance.application.provided.AccountCommand;
import com.easyplan.finance.application.provided.AccountFinder;
import com.easyplan.finance.application.required.repository.AccountOptionRepository;
import com.easyplan.finance.application.required.repository.AccountRepository;
import com.easyplan.finance.application.required.repository.CategoryRepository;
import com.easyplan.finance.domain.account.Account;
import com.easyplan.finance.domain.account.AccountBasicTemplate;
import com.easyplan.finance.domain.account.AccountOption;
import com.easyplan.finance.domain.account.AccountOptionTemplate;
import com.easyplan.finance.domain.account.AccountType;
import com.easyplan.finance.domain.account.Category;
import com.easyplan.finance.domain.account.exception.AccountErrorCode;
import com.easyplan.finance.domain.account.exception.AccountException;
import com.easyplan.finance.domain.account.request.AccountRequest.AccountCreateRequest;
import com.easyplan.finance.domain.account.request.AccountRequest.AccountUpdateRequest;
import com.easyplan.finance.domain.ledger.Ledger;
import com.easyplan.finance.domain.ledger.exception.LedgerErrorCode;
import com.easyplan.finance.domain.ledger.exception.LedgerException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountCommandService implements AccountCommand {
	private final CategoryRepository categoryRepo;
	
	private final AccountRepository accountRepo;
	
	private final AccountFinder accountFinder;
	
	private final AccountOptionRepository accountOptionRepo;

	private final AccountPolicy accountPolicy;
	
	private final AccountOptionCache optionCache;
	
	@Override
	public List<Category> createCategories(Ledger ledger) {
		List<Category> categories = Category.createDefault(ledger);
		
		return categoryRepo.saveAll(categories);
	}

	@Override
	public List<Account> memberSelectedCreateAccounts(Ledger ledger, List<Category> categories, List<AccountBasicTemplate> selectedAccounts) {
		Map<AccountType, Category> categoryMap = categories.stream()
				.collect(Collectors.toMap(Category::getAccountType, category -> category));
		
		int selectedAccountSize = selectedAccounts.size();
		
		List<Account> accounts = new ArrayList<>(selectedAccountSize + 3);
		
		boolean hasDefaultEquityAccount = selectedAccounts.contains(AccountBasicTemplate.EQU01);
		
		if(!hasDefaultEquityAccount) {
			Category equityCategory = getCategory(categoryMap, AccountType.EQUITY);
			AccountOption equityOption = getAccountOption(AccountOptionTemplate.EQUITY);
			
			accounts.add(Account.create(ledger, equityCategory, equityOption, AccountBasicTemplate.EQU01.getAccountName(), null));
		}
		
		selectedAccounts.stream()
				.map(basic -> {
						Category category = getCategory(categoryMap, basic.getAccountType());
						AccountOption option = getAccountOption(basic.getAccountOptionTemplate());
						
						return Account.create(ledger, category, option, basic.getAccountName(), null);
				})
				.forEach(accounts::add);
		
		if(accounts.size() != selectedAccountSize + 1) {
			throw new LedgerException(LedgerErrorCode.LEDGER_REGISTER_FAIL);
		}
		
		return accountRepo.saveAll(accounts);
	}

	@Override
	public Account createAccount(Ledger ledger, AccountCreateRequest accountCreate) {
		accountPolicy.validateAccountTypeMatch(accountCreate);
		
		Category category = categoryRepo.findByLedgerAndAccountType(ledger, accountCreate.accountType())
				.orElseThrow();
		
		// 해당 카테고리에 동일한 이름의 계정 항목이 있는지 검사
		accountPolicy.validateAccountName(category, accountCreate.accountName());
		
		AccountOption option = getAccountOption(accountCreate.optionCode());
		
		Account account = Account.create(ledger, category, option, accountCreate.accountName(), accountCreate.accountDescription());
		
		return accountRepo.save(account);
	}

	@Override
	public Account updateAccount(Ledger ledger, PublicId accountPublicId, AccountUpdateRequest accountUpdate) {
		Account account = accountFinder.findActiveAccount(ledger, accountPublicId);
		
		// 지원하는 계정 항목인지 검사
		accountPolicy.validateAccountTypeMatch(accountUpdate, account);
		
		AccountOption option = getAccountOption(accountUpdate.optionCode());
		
		// 해당 카테고리에 동일한 이름의 계정 항목이 있는지 검사
		accountPolicy.validateAccountNameForUpdate(account.getCategory(), accountUpdate.accountName(), account.getId());
		
		account.changeInfo(accountUpdate.accountName(), accountUpdate.accountDescription(), option);
		
		return accountRepo.save(account);
	}

	@Override
	public void deactivate(Ledger ledger, PublicId accountPublicId) {
		Account account = accountFinder.findActiveAccount(ledger, accountPublicId);
		
		account.deactivate();
	}
	
	private AccountOption getAccountOption(AccountOptionTemplate option) {
		Long optionId = optionCache.getId(option);
		
		return accountOptionRepo.getReferenceById(optionId);
	}
	
	private Category getCategory(Map<AccountType, Category> categoryMap, AccountType accountType) {
		Category category = categoryMap.get(accountType);
		
		if(category == null) {
			throw new AccountException(AccountErrorCode.CATEGORY_MAPPING_ERROR);
		}
		
		return category;
	}
	
}
