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
import com.easyplan.member.application.provided.MemberFinder;
import com.easyplan.member.application.provided.MemberSummary;
import com.easyplan.member.domain.MemberStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LedgerPolicyService implements LedgerPolicy {
	
	private final MemberFinder memberFinder;
	
	private final LedgerFinder ledgerFinder;
	
	@Override
	public Ledger validateAndCreateLedger(PublicId memberPublicId, LedgerCreateRequest ledgerCreate) {
		MemberSummary member = memberFinder.findByPublicIdSummary(memberPublicId);
		
		if(member.getStatus() != MemberStatus.ACTIVE) {
			throw new LedgerException(LedgerExceptionCode.MEMBER_NOT_VERIFIED);
		}
		
		int ledgerCount = ledgerFinder.countByOwnerId(member.getId());
		int ledgerMaxLimit = member.getRole().getMaxLimit();
		
		if(ledgerCount >= ledgerMaxLimit) {
			throw new LedgerException(LedgerExceptionCode.LEDGER_LIMIT_EXCEED);
		}
		
		return Ledger.create(member.getId(), ledgerCreate);
	}

	@Override
	public Ledger validateForUpdateLedger(PublicId memberPublicId, PublicId ledgerPublicId) {
		MemberSummary member = memberFinder.findByPublicIdSummary(memberPublicId);
		
		member.canUseService();
		
		Ledger ledger = ledgerFinder.findByLedgerPublicIdAndOwnerId(ledgerPublicId, member.getId())
				.orElseThrow(() -> new LedgerException(LedgerExceptionCode.LEDGER_NOT_FOUND));
		
		return ledger;
	}

	@Override
	public void validateForInfoLedger(PublicId memberPublicId, LedgerInfoUpdate ledgerInfoUpdate) {
		MemberSummary member = memberFinder.findByPublicIdSummary(memberPublicId);
		/*
		List<Ledger> myLedgers = ledgerFinder.findMyLedgers(member.getPublicId());
		
		String newName = ledgerInfoUpdate.name();
		
		for(Ledger ledger : myLedgers) {
			if(ledger.getName().equals(newName)) {
				throw new LedgerException(LedgerExceptionCode.LEDGER_NAME_DUPLICATE);
			}
		}
		*/
		
		if(ledgerFinder.existsByOwnerIdAndName(member.getId(), ledgerInfoUpdate.name())) {
			throw new LedgerException(LedgerExceptionCode.LEDGER_NAME_DUPLICATE);
		}
	}

}
