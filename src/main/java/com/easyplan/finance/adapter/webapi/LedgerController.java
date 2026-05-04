package com.easyplan.finance.adapter.webapi;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.easyplan._global.security.MemberDetails;
import com.easyplan._shared.domain.PublicId;
import com.easyplan._shared.response.GlobalResponse;
import com.easyplan.finance.adapter.webapi.response.LedgerCreateResponse;
import com.easyplan.finance.adapter.webapi.response.LedgerListResponse;
import com.easyplan.finance.adapter.webapi.response.LedgerListResponse.LedgerInfo;
import com.easyplan.finance.application.provided.ledger.LedgerCommand;
import com.easyplan.finance.application.provided.ledger.LedgerFinder;
import com.easyplan.finance.domain.ledger.request.LedgerCreateRequest;
import com.easyplan.finance.domain.ledger.request.LedgerUpdateRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ledgers")
public class LedgerController {
	
	private final LedgerCommand ledgerCommand;
	
	private final LedgerFinder ledgerFinder;
	
	@PostMapping("/{memberPublicId}")
	public GlobalResponse<LedgerCreateResponse> createLedgerAPI(
			@RequestBody LedgerCreateRequest ledgerCreate,
			@PathVariable String memberPublicId) {
		
		LedgerCreateResponse response = LedgerCreateResponse.of(ledgerCommand.createLedger(new PublicId(memberPublicId), ledgerCreate));
		
		return GlobalResponse.ok("가계부 생성이 완료되었습니다.", response);
	}
	
	// 내 가계부 목록 조회
	@GetMapping
	public GlobalResponse<LedgerListResponse> ledgerListAllAPI(
			@AuthenticationPrincipal MemberDetails member) {
		
		LedgerListResponse response = LedgerListResponse.of(ledgerFinder.findMyLedgers(new PublicId(member.getUsername())));
		
		return GlobalResponse.ok("", response);
	}
	
	// 내 가계부 단건 조회
	@GetMapping("/{ledgerPublicId}")
	public GlobalResponse<LedgerInfo> ledgerListAPI(@AuthenticationPrincipal MemberDetails member,
			@PathVariable String ledgerPublicId) {
		
		LedgerInfo response = LedgerInfo.from(ledgerFinder.findByLedgerPublicId(
				new PublicId(member.getUsername()),
				new PublicId(ledgerPublicId)
		));
		
		return GlobalResponse.ok("", response);
	}
	
	
	// 가계부 이름/설명 수정
	@PatchMapping("/{ledgerPublicId}")
	public GlobalResponse<LedgerInfo> ledgerInfoUpdateAPI(
			@AuthenticationPrincipal MemberDetails member,
			@PathVariable String ledgerPublicId,
			@RequestBody LedgerUpdateRequest.LedgerInfoUpdate ledgerInfo) {
		
		LedgerInfo response = LedgerInfo.from(ledgerCommand.updateLedgerInfo(
				new PublicId(member.getUsername()),
				new PublicId(ledgerPublicId),
				ledgerInfo
		));
		
		return GlobalResponse.ok("가계부 정보가 변경되었습니다.", response);
	}
	
	// 가계부 회계시작일 변경
	@PatchMapping("/fiscal/{ledgerPublicId}")
	public GlobalResponse<LedgerInfo> ledgerFiscalUpdateAPI(
			@AuthenticationPrincipal MemberDetails member,
			@PathVariable String ledgerPublicId,
			@RequestBody LedgerUpdateRequest.LedgerFiscalDayUpdate ledgerFiscal) {
		
		LedgerInfo response = LedgerInfo.from(ledgerCommand.updateLedgerFiscalDay(
				new PublicId(member.getUsername()),
				new PublicId(ledgerPublicId),
				ledgerFiscal
		));
		
		return GlobalResponse.ok("회계 시작일이 변경되었습니다.", response);
	}
	
	// 가계부 읽기전용으로(= 사용안함) 처리
	@PatchMapping("/archived/{ledgerPublicId}")
	public GlobalResponse<Void> ledgerArchivedAPI(
			@AuthenticationPrincipal MemberDetails member,
			@PathVariable String ledgerPublicId) {
		
		ledgerCommand.archived(new PublicId(member.getUsername()), new PublicId(ledgerPublicId));
		
		return GlobalResponse.ok("가계부가 '사용 안 함' 상태로 변경되었습니다.");
	}
	
	// 가계부 활성화 처리
	@PatchMapping("/reactivate/{ledgerPublicId}")
	public GlobalResponse<Void> ledgerReactivateAPI(
			@AuthenticationPrincipal MemberDetails member,
			@PathVariable String ledgerPublicId) {
		
		ledgerCommand.reactivate(new PublicId(member.getUsername()), new PublicId(ledgerPublicId));
		
		return GlobalResponse.ok("가계부가 '활성화' 상태로 변경되었습니다.");
	}
	
}