package com.easyplan.finance.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.application.provided.LedgerFinder;
import com.easyplan.finance.application.required.repository.LedgerRepository;
import com.easyplan.finance.domain.ledger.Ledger;
import com.easyplan.finance.domain.ledger.exception.LedgerErrorCode;
import com.easyplan.finance.domain.ledger.exception.LedgerException;
import com.easyplan.member.application.provided.MemberFinder;
import com.easyplan.member.application.provided.MemberSummary;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LedgerFinderService implements LedgerFinder {
	private final MemberFinder memberFinder;
	
	private final LedgerRepository ledgerRepo;

	@Override
	public Ledger findByLedger(PublicId ledgerPublicId) {
		return ledgerRepo.findByLedgerPublicId(ledgerPublicId)
				.orElseThrow(() -> new LedgerException(LedgerErrorCode.LEDGER_NOT_FOUND));
	}

	@Override
	public Ledger findByLedgerOwner(PublicId memberPublicId, PublicId ledgerPublicId) {
		MemberSummary member = memberFinder.findActiveMember(memberPublicId);
		
		Ledger ledger = ledgerRepo.findByLedgerPublicId(ledgerPublicId)
				.orElseThrow(() -> new LedgerException(LedgerErrorCode.LEDGER_NOT_FOUND));
		
		ledger.validateOwner(member.getId());
		
		return ledger;
	}
	
}
