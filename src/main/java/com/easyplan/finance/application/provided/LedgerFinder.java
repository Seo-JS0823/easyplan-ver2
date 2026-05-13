package com.easyplan.finance.application.provided;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.domain.ledger.Ledger;

public interface LedgerFinder {
	Ledger findByLedger(PublicId ledgerPublicId);
	
	Ledger findByLedgerOwner(Long memberId, PublicId ledgerPublicId);
}
