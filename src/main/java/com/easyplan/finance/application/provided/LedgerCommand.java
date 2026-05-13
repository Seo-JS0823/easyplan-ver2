package com.easyplan.finance.application.provided;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.domain.ledger.Ledger;
import com.easyplan.finance.domain.ledger.request.LedgerCreateRequest;
import com.easyplan.finance.domain.ledger.request.LedgerUpdateRequest.LedgerFiscalUpdate;
import com.easyplan.finance.domain.ledger.request.LedgerUpdateRequest.LedgerInfoUpdate;

public interface LedgerCommand {
	Ledger createLedger(Long memberId, LedgerCreateRequest ledgerCreate);
	
	Ledger updateInfo(Long memberId, PublicId ledgerPublicId, LedgerInfoUpdate ledgerInfo);
	
	Ledger updateFiscal(Long memberId, PublicId ledgerPublicId, LedgerFiscalUpdate ledgerFiscal);
	
	void delete(Long memberId, PublicId ledgerPublicId);
}
