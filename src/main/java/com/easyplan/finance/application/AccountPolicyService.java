package com.easyplan.finance.application;

import org.springframework.stereotype.Service;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.application.provided.account.AccountPolicy;
import com.easyplan.finance.application.provided.ledger.LedgerFinder;
import com.easyplan.finance.application.provided.ledger.LedgerPolicy;
import com.easyplan.finance.application.required.AccountCategoryRepository;
import com.easyplan.finance.application.required.AccountOptionRepository;
import com.easyplan.finance.application.required.AccountRepository;
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
import com.easyplan.member.application.provided.MemberPolicy;
import com.easyplan.member.application.provided.MemberSummary;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountPolicyService implements AccountPolicy {
	
	private final AccountCategoryRepository accountCategoryRepo;
	
	private final AccountOptionRepository accountOptionRepo;
	
	private final AccountRepository accountRepo;
	
	private final MemberPolicy memberPolicy;
	
	private final LedgerPolicy ledgerPolicy;
	
	private final LedgerFinder ledgerFinder;
	
	@Override
	public Account validateForCreateAccount(PublicId memberPublicId, PublicId ledgerPublicId, AccountCreateRequest accountCreate) {
		Ledger ledger = ledgerPolicy.validateForLedgerOwnership(memberPublicId, ledgerPublicId);
		
		AccountCategory category = accountCategoryRepo.findByLedgerIdAndAccountType(ledger.getId(), accountCreate.accountType());
		
		if(category == null) {
			throw new AccountException(AccountExceptionCode.CATEGORY_NOT_FOUND);
		}
		
		validateAccountOption(category.getAccountType(), accountCreate.optionCode());
		
		if(accountRepo.existsByCategoryIdAndAccountName(category.getId(), accountCreate.accountName())) {
			throw new AccountException(AccountExceptionCode.CATEGORY_WITHIN_ACCOUNT_NAME_DUPLICATE);
		}
		
		AccountOption option = accountOptionRepo.findByOptionCode(accountCreate.optionCode());
		
		return Account.create(ledger.getId(), category.getId(), option.getId(), accountCreate);
	}

	@Override
	public Account validateForUpdateAccount(PublicId memberPublicId, PublicId accountPublicId, AccountInfoUpdate accountInfo) {
		Account account = validateForAccountOwnership(memberPublicId, accountPublicId);
		
		if(!account.getAccountName().equals(accountInfo.accountName())) {
			if(accountRepo.existsByCategoryIdAndAccountName(account.getCategoryId(), accountInfo.accountName())) {
				throw new AccountException(AccountExceptionCode.CATEGORY_WITHIN_ACCOUNT_NAME_DUPLICATE);
			}
		}
		
		AccountCategory category = accountCategoryRepo.findById(account.getCategoryId())
				.orElseThrow(() -> new AccountException(AccountExceptionCode.CATEGORY_NOT_FOUND));
		
		validateAccountOption(category.getAccountType(), accountInfo.optionCode());
		
		return account;
	}

	@Override
	public Account validateForAccountOwnership(PublicId memberPublicId, PublicId accountPublicId) {
		MemberSummary member = memberPolicy.canUseService(memberPublicId);
		
		Account account = accountRepo.findByAccountPublicId(accountPublicId);
		
		if(account == null) {
			throw new AccountException(AccountExceptionCode.ACCOUNT_NOT_FOUND);
		}
		
		Ledger targetAccountLedger = ledgerFinder.findByid(account.getLedgerId())
				.orElseThrow(() -> new AccountException(AccountExceptionCode.ACCOUNT_NOT_FOUND));
		
		Ledger ledger = ledgerPolicy.validateForLedgerOwnership(memberPublicId, targetAccountLedger.getLedgerPublicId());
		
		if(!member.getId().equals(ledger.getOwnerId())) {
			throw new AccountException(AccountExceptionCode.ACCOUNT_NOT_FOUND);
		}
		
		return account;
	}
	
	//지원하는 계정 옵션인지 검증
	private void validateAccountOption(AccountType accountType, AccountOptionTemplate optionCode) {
		if(accountType != optionCode.getAccountType()) {
			throw new AccountException(AccountExceptionCode.ACCOUNT_NOT_SUPPORTED_OPTION);
		}
	}
	
}
