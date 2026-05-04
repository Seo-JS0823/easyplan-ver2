package com.easyplan.finance.application;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.application.provided.ledger.LedgerCommand;
import com.easyplan.finance.application.provided.ledger.LedgerPolicy;
import com.easyplan.finance.application.required.AccountCategoryRepository;
import com.easyplan.finance.application.required.AccountOptionRepository;
import com.easyplan.finance.application.required.AccountRepository;
import com.easyplan.finance.application.required.LedgerRepository;
import com.easyplan.finance.domain.account.Account;
import com.easyplan.finance.domain.account.AccountCategory;
import com.easyplan.finance.domain.account.AccountOption;
import com.easyplan.finance.domain.account.AccountOptionTemplate;
import com.easyplan.finance.domain.account.AccountType;
import com.easyplan.finance.domain.ledger.Ledger;
import com.easyplan.finance.domain.ledger.request.LedgerCreateRequest;
import com.easyplan.finance.domain.ledger.request.LedgerUpdateRequest.LedgerFiscalDayUpdate;
import com.easyplan.finance.domain.ledger.request.LedgerUpdateRequest.LedgerInfoUpdate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LedgerCommandService implements LedgerCommand {
	
	private final AccountRepository accountRepo;
	
	private final AccountCategoryRepository accountCategoryRepo;
	
	private final AccountOptionRepository accountOptionRepo;
	
	private final LedgerRepository ledgerRepo;
	
	private final LedgerPolicy ledgerPolicy;
	
	@Override
	public Ledger createLedger(PublicId memberPublicId, LedgerCreateRequest ledgerCreate) {
		Ledger ledger = ledgerPolicy.validateAndCreateLedger(memberPublicId, ledgerCreate);
		ledgerRepo.save(ledger);
		
		List<AccountCategory> accountCategory = AccountCategory.createDefault(ledger.getId());
		accountCategoryRepo.saveAll(accountCategory);
		
		List<Account> accounts = memberSelectedAccounts(ledger.getId(), accountCategory, ledgerCreate);
		accountRepo.saveAll(accounts);
		
		return ledger;
	}
	
	private List<Account> memberSelectedAccounts(Long ledgerId, List<AccountCategory> accountCategory, LedgerCreateRequest ledgerCreate) {
		Map<AccountOptionTemplate, Long> optionMap = accountOptionRepo.findAll().stream()
				.collect(Collectors.toMap(
						AccountOption::getOptionCode,
						AccountOption::getId
				));
		
		Map<AccountType, Long> categoryMap = accountCategory.stream()
				.collect(Collectors.toMap(
						AccountCategory::getAccountType,
						AccountCategory::getId
				));
		
		List<Account> accounts = ledgerCreate.accounts().stream()
				.map(acc -> {
					Long categoryId = categoryMap.get(acc.getAccountType());
					Long optionId = optionMap.get(acc.getAccountOptionTemplate());
					
					if(categoryId == null || optionId == null) {
						log.error("LedgerCommand memberSelectedAccounts: 가계부 생성 중 데이터 매핑에 실패하였습니다. [AccountType: {}, OptionTemplate: {}, categoryId: {}, optionId: {}]",
								acc.getAccountType(), acc.getAccountOptionTemplate(), categoryId, optionId
						);
						throw new IllegalStateException("가계부 생성중 시스템 에러가 발생했습니다. 잠시 후 다시 시도해주세요.");
					}
					
					return Account.create(ledgerId, categoryId, optionId, acc.getAccountName(), null);
				})
				.toList();
		
		return accounts;
	}

	@Override
	public Ledger updateLedgerInfo(PublicId memberPublicId, PublicId ledgerPublicId, LedgerInfoUpdate ledgerInfo) {
		Ledger ledger = ledgerPolicy.validateForUpdateLedger(memberPublicId, ledgerPublicId);
		
		ledgerPolicy.validateForInfoLedger(memberPublicId, ledgerInfo);
		
		ledger.changeInfo(ledgerInfo.name(), ledgerInfo.description());
		
		return ledgerRepo.save(ledger);
	}

	@Override
	public Ledger archived(PublicId memberPublicId, PublicId ledgerPublicId) {
		Ledger ledger = ledgerPolicy.validateForUpdateLedger(memberPublicId, ledgerPublicId);
		
		ledger.archived();
		
		return ledgerRepo.save(ledger);
	}

	@Override
	public Ledger updateLedgerFiscalDay(PublicId memberPublicId, PublicId ledgerPublicId, LedgerFiscalDayUpdate ledgerFiscal) {
		Ledger ledger = ledgerPolicy.validateForUpdateLedger(memberPublicId, ledgerPublicId);
		
		ledger.changeFiscalDay(ledgerFiscal.fiscalDay());
		
		return ledgerRepo.save(ledger);
	}

	@Override
	public Ledger reactivte(PublicId memberPublicId, PublicId ledgerPublicId) {
		Ledger ledger = ledgerPolicy.validateForUpdateLedger(memberPublicId, ledgerPublicId);
		
		ledger.reactivate();
		
		return ledgerRepo.save(ledger);
	}
	
	
}
