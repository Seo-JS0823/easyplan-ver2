package com.easyplan.finance.application.service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.application.provided.AccountFinder;
import com.easyplan.finance.application.required.AccountRepository;
import com.easyplan.finance.domain.EntrySide;
import com.easyplan.finance.domain.account.Account;
import com.easyplan.finance.domain.account.AccountStatus;
import com.easyplan.finance.domain.account.exception.AccountErrorCode;
import com.easyplan.finance.domain.account.exception.AccountException;
import com.easyplan.finance.domain.ledger.Ledger;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountFinderService implements AccountFinder {

	private final AccountRepository accountRepo;
	
	@Override
	public Account findAccount(Ledger ledger, PublicId accountPublicId) {
		return accountRepo.findByLedgerAndAccountPublicId(ledger, accountPublicId)
				.orElseThrow(() -> new AccountException(AccountErrorCode.ACCOUNT_NOT_FOUND));
	}

	@Override
	public Account findActiveAccount(Ledger ledger, PublicId accountPublicId) {
		return accountRepo.findByLedgerAndAccountPublicIdAndStatus(ledger, accountPublicId, AccountStatus.ACTIVE)
				.orElseThrow(() -> new AccountException(AccountErrorCode.ACCOUNT_NOT_FOUND));
	}

	@Override
	public Map<EntrySide, Account> findAccountFromJournal(Ledger ledger, Map<EntrySide, String> entries) {
		List<PublicId> publicIds = entries.values().stream()
				.map(publicId -> new PublicId(publicId))
				.toList();
		
		List<Account> accounts = accountRepo.findByLedgerAndAccountPublicIdIn(ledger, publicIds);
		
		Map<PublicId, Account> accountMap = accounts.stream()
				.collect(Collectors.toMap(
						Account::getAccountPublicId,
						acc -> acc
		));
		
		Map<EntrySide, Account> result = new EnumMap<>(EntrySide.class);
		entries.forEach((side, publicId) -> {
			Account account = accountMap.get(new PublicId(publicId));
			if(account != null) {
				result.put(side, account);
			}
		});
		
		return result;
	}
}
