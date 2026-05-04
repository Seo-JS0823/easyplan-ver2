package com.easyplan.finance.adapter.webapi;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.easyplan._global.security.MemberDetails;
import com.easyplan._shared.domain.PublicId;
import com.easyplan._shared.response.GlobalResponse;
import com.easyplan.finance.adapter.webapi.response.AccountCreateResponse;
import com.easyplan.finance.adapter.webapi.response.AccountListResponse;
import com.easyplan.finance.adapter.webapi.response.AccountResponse;
import com.easyplan.finance.application.provided.account.AccountCommand;
import com.easyplan.finance.application.provided.account.AccountFinder;
import com.easyplan.finance.domain.account.Account;
import com.easyplan.finance.domain.account.request.AccountCreateRequest;
import com.easyplan.finance.domain.account.request.AccountUpdateRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
public class AccountController {
	private final AccountCommand accountCommand;
	
	private final AccountFinder accountFinder;
	
	// 계정 항목 생성
	@PostMapping("/{ledgerPublicId}")
	public GlobalResponse<AccountCreateResponse> createAccountAPI(
			@AuthenticationPrincipal MemberDetails member,
			@PathVariable String ledgerPublicId,
			@RequestBody AccountCreateRequest accountCreate) {
		
		AccountCreateResponse response = AccountCreateResponse.of(accountCommand.createAccount(
				new PublicId(member.getUsername()),
				new PublicId(ledgerPublicId),
				accountCreate
		));
		
		return GlobalResponse.ok("계정 항목이 생성되었습니다.", response);
	}
	
	// 해당 가계부 계정 전체 조회
	@GetMapping("/{ledgerPublicId}")
	public GlobalResponse<AccountListResponse> accountListAllAPI(
			@AuthenticationPrincipal MemberDetails member,
			@PathVariable String ledgerPublicId) {
		
		AccountListResponse response = AccountListResponse.of(accountFinder.findByLedgerPublicId(
				new PublicId(member.getUsername()),
				new PublicId(ledgerPublicId)
		));
		
		return GlobalResponse.ok("", response);
	}
	
	// 계정 한 건 조회
	@GetMapping
	public GlobalResponse<AccountResponse> accountOneAPI(
			@AuthenticationPrincipal MemberDetails member,
			@RequestParam String accountPublicId) {
		
		AccountResponse response = AccountResponse.of(accountFinder.findOneByAccountPublicId(
				new PublicId(member.getUsername()),
				new PublicId(accountPublicId)
		));
		
		return GlobalResponse.ok("", response);
	}
	
	// 계정 정보 변경
	@PatchMapping("/{accountPublicId}")
	public GlobalResponse<AccountResponse> accountInfoUpdateAPI(
			@AuthenticationPrincipal MemberDetails member,
			@PathVariable String accountPublicId,
			@RequestBody AccountUpdateRequest.AccountInfoUpdate accountInfo) {
		
		AccountResponse response = AccountResponse.of(accountCommand.updateAccountInfo(
				new PublicId(member.getUsername()),
				new PublicId(accountPublicId),
				accountInfo
		));
		
		return GlobalResponse.ok("계정 항목 정보가 변경되었습니다.", response);
	}
	
	// 계정 비활성화 처리
	@PatchMapping("/deactivate")
	public GlobalResponse<AccountResponse> accountDeactivateAPI(
			@AuthenticationPrincipal MemberDetails member,
			@RequestParam String accountPublicId) {
		
		AccountResponse response = AccountResponse.of(accountCommand.deactivate(
				new PublicId(member.getUsername()),
				new PublicId(accountPublicId)
		));
		
		return GlobalResponse.ok("해당 계정 항목이 비활성화 처리되었습니다.", response);
	}
	
	// 비활성화 계정 조회
	@GetMapping("/deactivate")
	public GlobalResponse<AccountListResponse> accountDeactivateListAPI(
			@AuthenticationPrincipal MemberDetails member,
			@RequestParam String ledgerPublicId) {
		
		AccountListResponse response = AccountListResponse.of(accountFinder.findByLedgerIdAndDeactivate(
				new PublicId(member.getUsername()),
				new PublicId(ledgerPublicId)
		));
		
		return GlobalResponse.ok("", response);
	}
	
	// 계정 활성화 처리
	@PatchMapping("/reactivate")
	public GlobalResponse<AccountResponse> accountReactivateAPI(
			@AuthenticationPrincipal MemberDetails member,
			@RequestParam String accountPublicId) {
		
		AccountResponse response = AccountResponse.of(accountCommand.reactivate(
				new PublicId(member.getUsername()),
				new PublicId(accountPublicId)
		));
		
		return GlobalResponse.ok("해당 계정 항목이 활성화되었습니다.", response);
	}
	
}
