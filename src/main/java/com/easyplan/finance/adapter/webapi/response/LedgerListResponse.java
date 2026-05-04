package com.easyplan.finance.adapter.webapi.response;

import java.util.List;

import com.easyplan.finance.domain.ledger.Ledger;
import com.easyplan.finance.domain.ledger.LedgerType;

public record LedgerListResponse(List<LedgerInfo> ledgers) {
	
  public static LedgerListResponse of(List<Ledger> ledgers) {
    return new LedgerListResponse(
        ledgers.stream()
               .map(LedgerInfo::from)
               .toList()
    );
  }

  public static record LedgerInfo(String ledgerPublicId, LedgerType type, String name, String description, int fiscalDay) {
    public static LedgerInfo from(Ledger ledger) {
        return new LedgerInfo(
            ledger.getLedgerPublicId().publicId(),
            ledger.getType(),
            ledger.getName(),
            ledger.getDescription(),
            ledger.getFiscalStartDay().getDay()
        );
    }
  }
  
}
