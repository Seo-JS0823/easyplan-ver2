package com.easyplan.finance.application;

import org.springframework.stereotype.Service;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.application.provided.account.AccountFinder;
import com.easyplan.finance.application.provided.account.AccountPolicy;
import com.easyplan.finance.application.provided.ledger.LedgerFinder;
import com.easyplan.finance.application.required.AccountCategoryRepository;
import com.easyplan.finance.application.required.AccountOptionRepository;
import com.easyplan.finance.domain.account.Account;
import com.easyplan.finance.domain.account.AccountCategory;
import com.easyplan.finance.domain.account.AccountOption;
import com.easyplan.finance.domain.account.AccountOptionTemplate;
import com.easyplan.finance.domain.account.AccountType;
import com.easyplan.finance.domain.account.exception.AccountException;
import com.easyplan.finance.domain.account.exception.AccountExceptionCode;
import com.easyplan.finance.domain.account.request.AccountCreateRequest;
import com.easyplan.finance.domain.account.request.AccountUpdateRequest.AccountInfoUpdate;
import com.easyplan.finance.domain.ledger.Ledger;
import com.easyplan.finance.domain.ledger.exception.LedgerException;
import com.easyplan.finance.domain.ledger.exception.LedgerExceptionCode;
import com.easyplan.member.application.provided.MemberFinder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountPolicyService implements AccountPolicy {
	
	private final LedgerFinder ledgerFinder;
	
	private final AccountCategoryRepository accountCategoryRepo;
	
	private final AccountOptionRepository accountOptionRepo;
	
	private final AccountFinder accountFinder;
	
	private final MemberFinder memberFinder;
	
	@Override
	public Account validateForCreateAccount(PublicId ledgerPublicId, AccountCreateRequest accountCreate) {
		Ledger ledger = ledgerFinder.findByLedgerPublicId(ledgerPublicId);
		
		AccountCategory category = accountCategoryRepo.findByLedgerIdAndAccountType(ledger.getId(), accountCreate.accountType());
		
		if(category == null) {
			throw new AccountException(AccountExceptionCode.CATEGORY_NOT_FOUND);
		}
		
		validateAccountOption(category.getAccountType(), accountCreate.optionCode());
		
		if(accountFinder.existsByCategoryIdAndAccountName(category.getId(), accountCreate.accountName())) {
			throw new AccountException(AccountExceptionCode.CATEGORY_WITHIN_ACCOUNT_NAME_DUPLICATE);
		}
		
		AccountOption option = accountOptionRepo.findByOptionCode(accountCreate.optionCode());
		
		return Account.create(ledger.getId(), category.getId(), option.getId(), accountCreate);
	}

	@Override
	public Account validateForUpdateAccount(PublicId memberPublicId, PublicId accountPublicId, AccountInfoUpdate accountInfo) {
		Account account = accountFinder.findByAccountPublicId(accountPublicId);
		
		if(account == null) {
			throw new AccountException(AccountExceptionCode.ACCOUNT_NOT_FOUND);
		}
		
		Long ownerId = ledgerFinder.findByid(account.getLedgerId())
				.orElseThrow(() -> new LedgerException(LedgerExceptionCode.LEDGER_NOT_FOUND))
				.getOwnerId();
		
		Long memberId = memberFinder.findByPublicId(memberPublicId).getId();
		
		if(!memberId.equals(ownerId)) {
			throw new AccountException(AccountExceptionCode.ACCOUNT_NOT_FOUND);
		}
		
		if(!account.getAccountName().equals(accountInfo.accountName())) {
			if(accountFinder.existsByCategoryIdAndAccountName(account.getCategoryId(), accountInfo.accountName())) {
				throw new AccountException(AccountExceptionCode.CATEGORY_WITHIN_ACCOUNT_NAME_DUPLICATE);
			}
		}
		
		AccountCategory category = accountCategoryRepo.findById(account.getCategoryId())
				.orElseThrow();
		
		validateAccountOption(category.getAccountType(), accountInfo.optionCode());
		
		return account;
	}
	
	// 지원하는 계정 옵션인지 검증
	private void validateAccountOption(AccountType accountType, AccountOptionTemplate optionCode) {
		if(accountType != optionCode.getAccountType()) {
			throw new AccountException(AccountExceptionCode.ACCOUNT_NOT_SUPPORTED_OPTION);
		}
	}
	
}
