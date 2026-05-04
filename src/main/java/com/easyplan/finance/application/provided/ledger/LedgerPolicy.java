package com.easyplan.finance.application.provided.ledger;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.domain.ledger.Ledger;
import com.easyplan.finance.domain.ledger.request.LedgerCreateRequest;
import com.easyplan.finance.domain.ledger.request.LedgerUpdateRequest.LedgerInfoUpdate;

public interface LedgerPolicy {
	void validateForLedgerOwnership(PublicId memberPublicId, PublicId ledgerPublicId);
	
	Ledger validateAndCreateLedger(PublicId memberPublicId, LedgerCreateRequest ledgerCreate);
	
	Ledger validateForUpdateLedger(PublicId memberPublicId, PublicId ledgerPublicId);

	void validateForInfoLedger(PublicId memberPublicId, LedgerInfoUpdate ledgerInfo);
}
