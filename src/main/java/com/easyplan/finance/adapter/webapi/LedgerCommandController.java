package com.easyplan.finance.adapter.webapi;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.easyplan._global.security.MemberDetails;
import com.easyplan._shared.domain.PublicId;
import com.easyplan._shared.response.GlobalResponse;
import com.easyplan.finance.application.usecase.FinanceCommand;
import com.easyplan.finance.application.usecase.response.command.LedgerResponse.LedgerCreateResponse;
import com.easyplan.finance.application.usecase.response.command.LedgerResponse.LedgerFiscalUpdateResponse;
import com.easyplan.finance.application.usecase.response.command.LedgerResponse.LedgerInfoUpdateResponse;
import com.easyplan.finance.domain.ledger.request.LedgerCreateRequest;
import com.easyplan.finance.domain.ledger.request.LedgerUpdateRequest.LedgerFiscalUpdate;
import com.easyplan.finance.domain.ledger.request.LedgerUpdateRequest.LedgerInfoUpdate;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ledgers")
public class LedgerCommandController {
	private final FinanceCommand financeCommand;
	
	@PostMapping("")
	public GlobalResponse<LedgerCreateResponse> createLedger(
			@AuthenticationPrincipal MemberDetails member,
			@RequestBody LedgerCreateRequest ledgerCreate) {
		
		Long memberId = member.getMemberId();
		
		LedgerCreateResponse response = financeCommand.createLedger(memberId, ledgerCreate);
		
		return GlobalResponse.ok("가계부가 생성되었습니다.", response);
	}
	
	@PatchMapping("/info/{ledgerPublicId}")
	public GlobalResponse<LedgerInfoUpdateResponse> updateLedgerInfo(
			@AuthenticationPrincipal MemberDetails member,
			@RequestBody LedgerInfoUpdate ledgerInfo,
			@PathVariable String ledgerPublicId) {
		
		Long memberId = member.getMemberId();
		
		LedgerInfoUpdateResponse response = financeCommand.updateLedgerInfo(memberId, new PublicId(ledgerPublicId), ledgerInfo);
		
		return GlobalResponse.ok("가계부 정보가 수정되었습니다.", response);
	}
	
	@PatchMapping("/fiscal/{ledgerPublicId}")
	public GlobalResponse<LedgerFiscalUpdateResponse> updateLedgerFiscal(
			@AuthenticationPrincipal MemberDetails member,
			@RequestBody LedgerFiscalUpdate ledgerFiscal,
			@PathVariable String ledgerPublicId) {
		
		Long memberId = member.getMemberId();
		
		LedgerFiscalUpdateResponse response = financeCommand.updateLedgerFiscal(memberId, new PublicId(ledgerPublicId), ledgerFiscal);
		
		return GlobalResponse.ok("회계 시작일이 변경되었습니다.", response);
	}
	
	@DeleteMapping("/{ledgerPublicId}")
	public GlobalResponse<Void> deleteLedger(
			@AuthenticationPrincipal MemberDetails member,
			@PathVariable String ledgerPublicId) {
		
		Long memberId = member.getMemberId();
		
		financeCommand.deleteLedger(memberId, new PublicId(ledgerPublicId));
		
		return GlobalResponse.ok("가계부가 삭제되었습니다.");
	}
	
}
