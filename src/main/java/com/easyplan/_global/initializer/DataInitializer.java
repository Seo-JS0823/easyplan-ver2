package com.easyplan._global.initializer;

import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.easyplan.finance.application.required.AccountOptionRepository;
import com.easyplan.finance.domain.account.AccountOption;
import com.easyplan.finance.domain.account.AccountOptionTemplate;
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
	
	private final MemberRepository memberRepo;
	
	private final PasswordEncoder passwordEncoder;
	
	private final MemberPolicy memberPolicy;
	
	@Override
	public void run(String... args) throws Exception {
		if(accountOptionRepo.count() == 0) {
			List<AccountOption> defaultOptions = AccountOptionTemplate.defaultOptions();
			
			accountOptionRepo.saveAll(defaultOptions);			
		}
		
		if(memberRepo.count() == 0) {
			MemberRegisterRequest testMember = new MemberRegisterRequest(
					"test@test.com", "password01@", "nickname",
					"introduction", LocalDate.of(2000, 1, 1), "Asia/Seoul",
					true, true
			);
			
			Member member = Member.register(testMember, passwordEncoder, memberPolicy);
			member.activate();
			member.changeRole(MemberRole.BRONZE);
			
			memberRepo.save(member);
		}
	}
}
