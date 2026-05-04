package com.easyplan.finance.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.application.provided.ledger.LedgerFinder;
import com.easyplan.finance.application.provided.ledger.LedgerPolicy;
import com.easyplan.finance.domain.ledger.Ledger;
import com.easyplan.finance.domain.ledger.exception.LedgerException;
import com.easyplan.finance.domain.ledger.exception.LedgerExceptionCode;
import com.easyplan.finance.domain.ledger.request.LedgerCreateRequest;
import com.easyplan.finance.domain.ledger.request.LedgerUpdateRequest.LedgerInfoUpdate;
import com.easyplan.member.application.provided.MemberPolicy;
import com.easyplan.member.application.provided.MemberSummary;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LedgerPolicyService implements LedgerPolicy {
	
	private final MemberPolicy memberPolicy;
	
	private final LedgerFinder ledgerFinder;
	
	@Override
	public Ledger validateForLedgerCreate(PublicId memberPublicId, LedgerCreateRequest ledgerCreate) {
		MemberSummary member = memberPolicy.canUseService(memberPublicId);
		
		int ledgerCount = ledgerFinder.countByOwnerId(member.getId());
		int ledgerMaxLimit = member.getRole().getMaxLimit();
		
		if(ledgerCount >= ledgerMaxLimit) {
			throw new LedgerException(LedgerExceptionCode.LEDGER_LIMIT_EXCEED);
		}
		
		if(ledgerFinder.existsByOwnerIdAndName(member.getId(), ledgerCreate.name())) {
			throw new LedgerException(LedgerExceptionCode.LEDGER_NAME_DUPLICATE);
		}
		
		return Ledger.create(member.getId(), ledgerCreate);
	}

	@Override
	public Ledger validateForUpdateLedger(PublicId memberPublicId, PublicId ledgerPublicId) {
		MemberSummary member = memberPolicy.canUseService(memberPublicId);
		
		Ledger ledger = ledgerFinder.findByLedgerPublicIdAndOwnerId(ledgerPublicId, member.getId())
				.orElseThrow(() -> new LedgerException(LedgerExceptionCode.LEDGER_NOT_FOUND));
		
		return ledger;
	}

	@Override
	public Ledger validateForInfoLedger(PublicId memberPublicId, PublicId ledgerPublicId, LedgerInfoUpdate ledgerInfoUpdate) {
		// MemberSummary member = memberPolicy.canUseService(memberPublicId);
		/*
		List<Ledger> myLedgers = ledgerFinder.findMyLedgers(member.getPublicId());
		
		String newName = ledgerInfoUpdate.name();
		
		for(Ledger ledger : myLedgers) {
			if(ledger.getName().equals(newName)) {
				throw new LedgerException(LedgerExceptionCode.LEDGER_NAME_DUPLICATE);
			}
		}
		*/
		
		Ledger ledger = validateForUpdateLedger(memberPublicId, ledgerPublicId);
		
		if(!ledger.getName().equals(ledgerInfoUpdate.name())) {
			MemberSummary member = memberPolicy.canUseService(memberPublicId);
			
			if(ledgerFinder.existsByOwnerIdAndName(member.getId(), ledgerInfoUpdate.name())) {
				throw new LedgerException(LedgerExceptionCode.LEDGER_NAME_DUPLICATE);
			}			
		}
		
		return ledger;
	}

	@Override
	public Ledger validateForLedgerOwnership(PublicId memberPublicId, PublicId ledgerPublicId) {
		MemberSummary member = memberPolicy.canUseService(memberPublicId);
		
		return ledgerFinder.findByOwnerIdAndLedgerPublicId(member.getId(), ledgerPublicId)
				.orElseThrow(() -> new LedgerException(LedgerExceptionCode.LEDGER_NOT_FOUND));
		
		// TODO: 공유가계부 테이블 만들면 변수 할당해서 중간 테이블 조회 및 참여자 정보 확인해서 리턴
	}
	
}
