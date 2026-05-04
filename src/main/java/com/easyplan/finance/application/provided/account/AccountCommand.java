package com.easyplan.finance.application.provided.account;

import com.easyplan.finance.domain.account.Account;

public interface AccountCommand {
	Account createAccount();
	
	Account updateAccountInfo();
	
	Account deactivate();
	
	Account reactivate();
}
