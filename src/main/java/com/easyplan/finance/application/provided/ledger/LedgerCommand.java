package com.easyplan.finance.application.provided.ledger;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.domain.ledger.Ledger;
import com.easyplan.finance.domain.ledger.request.LedgerCreateRequest;
import com.easyplan.finance.domain.ledger.request.LedgerUpdateRequest;

public interface LedgerCommand {
	Ledger createLedger
		(PublicId memberPublicId, LedgerCreateRequest ledgerCreate);
	
	Ledger updateLedgerInfo
		(PublicId memberPublicId, PublicId ledgerPublicId, LedgerUpdateRequest.LedgerInfoUpdate ledgerInfo);
	
	Ledger archived
		(PublicId memberPublicId, PublicId ledgerPublicId);
	
	Ledger updateLedgerFiscalDay
		(PublicId memberPublicId, PublicId ledgerPublicId, LedgerUpdateRequest.LedgerFiscalDayUpdate ledgerFiscal);
	
	Ledger reactivte
		(PublicId memberPublicId, PublicId ledgerPublicId);
}
