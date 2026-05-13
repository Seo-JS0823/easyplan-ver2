package com.easyplan.finance.application.service;

import org.springframework.stereotype.Service;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.application.policy.LedgerPolicy;
import com.easyplan.finance.application.provided.LedgerCommand;
import com.easyplan.finance.application.provided.LedgerFinder;
import com.easyplan.finance.application.required.repository.LedgerRepository;
import com.easyplan.finance.domain.ledger.Ledger;
import com.easyplan.finance.domain.ledger.request.LedgerCreateRequest;
import com.easyplan.finance.domain.ledger.request.LedgerUpdateRequest.LedgerFiscalUpdate;
import com.easyplan.finance.domain.ledger.request.LedgerUpdateRequest.LedgerInfoUpdate;
import com.easyplan.member.application.provided.MemberFinder;
import com.easyplan.member.application.provided.MemberSummary;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LedgerCommandService implements LedgerCommand {
	
	private final MemberFinder memberFinder;
	
	private final LedgerFinder ledgerFinder;
	
	private final LedgerRepository ledgerRepo;
	
	private final LedgerPolicy ledgerPolicy;
	
	@Override
	public Ledger createLedger(Long memberId, LedgerCreateRequest ledgerCreate) {
		// 서비스 사용 가능한 멤버 조회
		MemberSummary member = memberFinder.findActiveMember(memberId);
		
		// 보유한 가계부 중 동일한 이름이 있는지 검사
		ledgerPolicy.validateLedgerName(member.getId(), ledgerCreate.name());
		
		// 가계부 생성
		Ledger ledger = Ledger.create(member.getId(), ledgerCreate);
		
		// 생성
		return ledgerRepo.save(ledger);
	}

	@Override
	public Ledger updateInfo(Long memberId, PublicId ledgerPublicId, LedgerInfoUpdate ledgerInfo) {
		// 서비스 사용 가능한 멤버 조회
		MemberSummary member = memberFinder.findActiveMember(memberId);
		
		// 가계부 조회
		Ledger ledger = ledgerFinder.findByLedger(ledgerPublicId);
		
		// 소유권 검증
		ledger.validateOwner(member.getId());
		
		// 보유한 가계부 중 동일한 이름이 있는지 검사
		ledgerPolicy.validateLedgerNameForUpdate(member.getId(), ledger.getId(), ledgerInfo.name());
		
		// 업데이트
		ledger.changeInfo(ledgerInfo.name(), ledgerInfo.description());
		
		return ledgerRepo.save(ledger);
	}

	@Override
	public Ledger updateFiscal(Long memberId, PublicId ledgerPublicId, LedgerFiscalUpdate ledgerFiscal) {
		// 서비스 사용 가능한 멤버 조회
		MemberSummary member = memberFinder.findActiveMember(memberId);
		
		// 가계부 조회
		Ledger ledger = ledgerFinder.findByLedger(ledgerPublicId);
		
		// 소유권 검증
		ledger.validateOwner(member.getId());
		
		// 업데이트
		ledger.changeFiscalDay(ledgerFiscal.fiscalDay());
		
		return ledgerRepo.save(ledger);
	}

	@Override
	public void delete(Long memberId, PublicId ledgerPublicId) {
		MemberSummary member = memberFinder.findActiveMember(memberId);
		
		Ledger ledger = ledgerFinder.findByLedger(ledgerPublicId);
		
		ledger.validateOwner(member.getId());
		
		ledgerRepo.delete(ledger);
	}
	
}
