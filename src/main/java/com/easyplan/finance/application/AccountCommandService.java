package com.easyplan.finance.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.application.dto.AccountDetail;
import com.easyplan.finance.application.provided.account.AccountCommand;
import com.easyplan.finance.application.provided.account.AccountPolicy;
import com.easyplan.finance.application.required.AccountOptionRepository;
import com.easyplan.finance.application.required.AccountRepository;
import com.easyplan.finance.domain.account.Account;
import com.easyplan.finance.domain.account.AccountOption;
import com.easyplan.finance.domain.account.exception.AccountException;
import com.easyplan.finance.domain.account.exception.AccountExceptionCode;
import com.easyplan.finance.domain.account.request.AccountCreateRequest;
import com.easyplan.finance.domain.account.request.AccountUpdateRequest.AccountInfoUpdate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountCommandService implements AccountCommand {
	
	private final AccountOptionRepository accountOptionRepo;
	
	private final AccountRepository accountRepo;
	
	private final AccountPolicy accountPolicy;

	@Override
	public Account createAccount(PublicId memberPublicId, PublicId ledgerPublicId, AccountCreateRequest accountCreate) {
		Account account = accountPolicy.validateForCreateAccount(memberPublicId, ledgerPublicId, accountCreate);
		return accountRepo.save(account);
	}

	@Override
	public AccountDetail updateAccountInfo(PublicId memberPublicId, PublicId accountPublicId, AccountInfoUpdate accountInfo) {
		Account account = accountPolicy.validateForUpdateAccount(memberPublicId, accountPublicId, accountInfo);
		
		AccountOption option = accountOptionRepo.findByOptionCode(accountInfo.optionCode());
		
		account.changeAccountInfo(option.getId(), accountInfo);
		
		accountRepo.save(account);
		
		return AccountDetail.of(option, account);
	}

	@Override
	public AccountDetail deactivate(PublicId memberPublicId, PublicId accountPublicId) {
		Account account = accountPolicy.validateForAccountOwnership(memberPublicId, accountPublicId);
		
		account.deactivate();
		
		accountRepo.save(account);
		
		AccountOption option = accountOptionRepo.findById(account.getOptionId())
				.orElseThrow(() -> new AccountException(AccountExceptionCode.ACCOUNT_NOT_SUPPORTED_OPTION));
		
		return AccountDetail.of(option, account);
	}

	@Override
	public AccountDetail reactivate(PublicId memberPublicId, PublicId accountPublicId) {
		Account account = accountPolicy.validateForAccountOwnership(memberPublicId, accountPublicId);
		
		account.reactivate();
		
		accountRepo.save(account);
		
		AccountOption option = accountOptionRepo.findById(account.getOptionId())
				.orElseThrow(() -> new AccountException(AccountExceptionCode.ACCOUNT_NOT_SUPPORTED_OPTION));
		
		return AccountDetail.of(option, account);
	}
	
}
