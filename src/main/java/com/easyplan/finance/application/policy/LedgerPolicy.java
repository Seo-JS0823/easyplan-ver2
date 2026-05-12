package com.easyplan.finance.application.policy;

import org.springframework.stereotype.Component;

import com.easyplan.finance.application.required.repository.LedgerRepository;
import com.easyplan.finance.domain.ledger.exception.LedgerErrorCode;
import com.easyplan.finance.domain.ledger.exception.LedgerException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LedgerPolicy {
	private final LedgerRepository ledgerRepo;
	
	public void validateLedgerName(Long ledgerMemberId, String ledgerName) {
		if(ledgerRepo.existsByLedgerMemberIdAndLedgerName(ledgerMemberId, ledgerName)) {
			throw new LedgerException(LedgerErrorCode.LEDGER_NAME_DUPLICATE);
		}
	}
	
	public void validateLedgerNameForUpdate(Long ledgerMemberId, Long ledgerId, String ledgerName) {
		if(ledgerRepo.existsByLedgerMemberIdAndLedgerNameAndIdNot(ledgerMemberId, ledgerName, ledgerId)) {
			throw new LedgerException(LedgerErrorCode.LEDGER_NAME_DUPLICATE);
		}
	}
}
