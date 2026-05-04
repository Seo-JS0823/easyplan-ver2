package com.easyplan.finance.domain.ledger.request;

import java.util.List;

import com.easyplan.finance.domain.account.AccountBasicTemplate;
import com.easyplan.finance.domain.ledger.LedgerType;

public record LedgerCreateRequest(LedgerType type, String name, String description, List<AccountBasicTemplate> accounts) {

}
