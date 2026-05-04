package com.easyplan.finance.application.provided.ledger;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.domain.ledger.Ledger;
import com.easyplan.finance.domain.ledger.request.LedgerCreateRequest;
import com.easyplan.finance.domain.ledger.request.LedgerUpdateRequest.LedgerInfoUpdate;

public interface LedgerPolicy {
	Ledger validateForLedgerOwnership(PublicId memberPublicId, PublicId ledgerPublicId);
	
	Ledger validateForLedgerCreate(PublicId memberPublicId, LedgerCreateRequest ledgerCreate);
	
	Ledger validateForUpdateLedger(PublicId memberPublicId, PublicId ledgerPublicId);

	Ledger validateForInfoLedger(PublicId memberPublicId, PublicId ledgerPublicId, LedgerInfoUpdate ledgerInfo);
}
