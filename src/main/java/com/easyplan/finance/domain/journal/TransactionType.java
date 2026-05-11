package com.easyplan.finance.domain.journal;

import com.easyplan.finance.domain.account.Account;
import com.easyplan.finance.domain.account.AccountType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@RequiredArgsConstructor
@Slf4j
public enum TransactionType {
	EXPENSE("지출", "사용했습니다."),
	INCOME("수입", "수입이 발생하였습니다."),
	TRANSFER("이체", "이체되었습니다.")
	
	;
	private final String description;
	
	private final String transactionMessage;
	
	public boolean validateTransaction(Account debit, Account credit) {
		if(debit.getAccountType() == null || credit.getAccountType() == null) {
			log.error("거래 입력 중 EntryLine 의 AccountType 을 찾을 수 없습니다.");
			return false;
		}
		
		switch (this) {
			case EXPENSE:
				return debit.getAccountType() == AccountType.EXPENSE &&
							(credit.getAccountType() == AccountType.ASSET  ||
							 credit.getAccountType() == AccountType.LIABILITIES);
				
			case INCOME:
				return debit.getAccountType() == AccountType.ASSET &&
						   credit.getAccountType() == AccountType.INCOME;
			
			case TRANSFER:
				return debit.getAccountType() == AccountType.ASSET &&
				       credit.getAccountType() == AccountType.ASSET;
				
			default:
				return false;
		}
	}
}
