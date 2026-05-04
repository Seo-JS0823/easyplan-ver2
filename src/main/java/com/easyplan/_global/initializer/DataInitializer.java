package com.easyplan._global.initializer;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.easyplan.finance.application.required.AccountCategoryRepository;
import com.easyplan.finance.application.required.AccountOptionRepository;
import com.easyplan.finance.application.required.AccountRepository;
import com.easyplan.finance.application.required.LedgerRepository;
import com.easyplan.finance.domain.account.Account;
import com.easyplan.finance.domain.account.AccountBasicTemplate;
import com.easyplan.finance.domain.account.AccountCategory;
import com.easyplan.finance.domain.account.AccountOption;
import com.easyplan.finance.domain.account.AccountOptionTemplate;
import com.easyplan.finance.domain.account.AccountType;
import com.easyplan.finance.domain.ledger.Ledger;
import com.easyplan.finance.domain.ledger.LedgerType;
import com.easyplan.finance.domain.ledger.request.LedgerCreateRequest;
import com.easyplan.member.application.provided.MemberPolicy;
import com.easyplan.member.application.required.MemberRepository;
import com.easyplan.member.domain.Member;
import com.easyplan.member.domain.MemberRole;
import com.easyplan.member.domain.PasswordEncoder;
import com.easyplan.member.domain.request.MemberRegisterRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
	
	private final AccountOptionRepository accountOptionRepo;
	
	private final AccountCategoryRepository accountCategoryRepo;
	
	private final MemberRepository memberRepo;
	
	private final PasswordEncoder passwordEncoder;
	
	private final MemberPolicy memberPolicy;
	
	private final LedgerRepository ledgerRepo;
	
	private final AccountRepository accountRepo;
	
	@Override
	public void run(String... args) throws Exception {
		if(accountOptionRepo.count() == 0) {
			List<AccountOption> defaultOptions = AccountOptionTemplate.defaultOptions();
			
			accountOptionRepo.saveAll(defaultOptions);			
		}
		
		Member member = null;
		
		if(memberRepo.count() == 0) {
			MemberRegisterRequest testMember = new MemberRegisterRequest(
					"test@test.com", "password01@", "nickname",
					"introduction", LocalDate.of(2000, 1, 1), "Asia/Seoul",
					true, true
			);
			
			member = Member.register(testMember, passwordEncoder, memberPolicy);
			member.activate();
			member.changeRole(MemberRole.BRONZE);
			
			member = memberRepo.save(member);
		}
		
		if(ledgerRepo.count() == 0) {
			List<AccountBasicTemplate> accountTemplate = List.of(AccountBasicTemplate.values());
			
			LedgerCreateRequest testLedger = new LedgerCreateRequest(
					LedgerType.PERSONAL,
					"이지플랜 가계부",
					"이지플랜 가계부 초기화 데이터",
					accountTemplate
			);
			
			Ledger ledger = Ledger.create(member.getId(), testLedger);
			ledgerRepo.save(ledger);
			
			List<AccountCategory> accountCategory = AccountCategory.createDefault(ledger.getId());
			accountCategoryRepo.saveAll(accountCategory);
			
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
			
			List<Account> accounts = testLedger.accounts().stream()
					.map(acc -> {
						Long categoryId = categoryMap.get(acc.getAccountType());
						Long optionId = optionMap.get(acc.getAccountOptionTemplate());
						
						if(categoryId == null || optionId == null) {
							throw new IllegalStateException("가계부 생성중 시스템 에러가 발생했습니다. 잠시 후 다시 시도해주세요.");
						}
						
						return Account.create(ledger.getId(), categoryId, optionId, acc.getAccountName(), null);
					})
					.toList();
			
			accountRepo.saveAll(accounts);
		}
	}
}
