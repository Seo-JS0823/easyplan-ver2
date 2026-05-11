package com.easyplan.finance.application.provided;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.domain.ledger.Ledger;
import com.easyplan.finance.domain.ledger.request.LedgerCreateRequest;
import com.easyplan.finance.domain.ledger.request.LedgerUpdateRequest.LedgerFiscalUpdate;
import com.easyplan.finance.domain.ledger.request.LedgerUpdateRequest.LedgerInfoUpdate;

public interface LedgerCommand {
	Ledger createLedger(PublicId memberPublicId, LedgerCreateRequest ledgerCreate);
	
	Ledger updateInfo(PublicId memberPublicId, PublicId ledgerPublicId, LedgerInfoUpdate ledgerInfo);
	
	Ledger updateFiscal(PublicId memberPublicId, PublicId ledgerPublicId, LedgerFiscalUpdate ledgerFiscal);
	
	void delete(PublicId memberPublicId, PublicId ledgerPublicId);
}
