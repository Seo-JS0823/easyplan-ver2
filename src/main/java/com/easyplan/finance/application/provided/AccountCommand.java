package com.easyplan.finance.application.provided;

import java.util.List;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.domain.account.Account;
import com.easyplan.finance.domain.account.AccountBasicTemplate;
import com.easyplan.finance.domain.account.Category;
import com.easyplan.finance.domain.account.request.AccountRequest.AccountCreateRequest;
import com.easyplan.finance.domain.account.request.AccountRequest.AccountUpdateRequest;
import com.easyplan.finance.domain.ledger.Ledger;

public interface AccountCommand {
	// 가계부 생성 시 기본 생성되는 카테고리
	List<Category> createCategories(Ledger ledger);
	
	// 사용자가 선택한 계정 항목만 생성
	List<Account> memberSelectedCreateAccounts(Ledger ledger, List<Category> categories, List<AccountBasicTemplate> selectedAccounts);
	
	// 계정 항목 생성
	Account createAccount(Ledger ledger, AccountCreateRequest accountCreate);
	
	// 계정 정보 변경 Name, Description, Option
	Account updateAccount(Ledger ledger, PublicId accountPublicId, AccountUpdateRequest accountUpdate);
	
	// 계정 삭제
	void deactivate(Ledger ledger, PublicId accountPublicId);
}
