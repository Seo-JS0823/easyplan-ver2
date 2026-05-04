package com.easyplan.finance.application.provided.account;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.application.dto.AccountDetail;
import com.easyplan.finance.domain.account.Account;
import com.easyplan.finance.domain.account.request.AccountCreateRequest;
import com.easyplan.finance.domain.account.request.AccountUpdateRequest;

public interface AccountCommand {
	Account createAccount(PublicId memberPublicId, PublicId ledgerPublicId, AccountCreateRequest accountCreate);
	
	AccountDetail updateAccountInfo(PublicId memberPublicId, PublicId accountPublicId, AccountUpdateRequest.AccountInfoUpdate accountInfo);

	AccountDetail deactivate(PublicId memberPublicId, PublicId accountPublicId);

	AccountDetail reactivate(PublicId memberPublicId, PublicId accountPublicId);
}
