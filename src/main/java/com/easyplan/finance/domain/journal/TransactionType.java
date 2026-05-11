package com.easyplan.finance.domain.journal;

import com.easyplan._shared.util.MoneyFormatter;
import com.easyplan.finance.domain.EntrySide;
import com.easyplan.finance.domain.account.Account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@RequiredArgsConstructor
@Slf4j
public enum TransactionType {
	EXPENSE("지출"),
	INCOME("수입"),
	TRANSFER("이체")
	
	;
	private final String description;
	
	public String createdMessage(Journal journal) {
		Account debit = journal.getEntryLine(EntrySide.DEBIT).getAccount();
		Account credit = journal.getEntryLine(EntrySide.CREDIT).getAccount();
		
		switch (this) {
			case INCOME : {
				return String.format("[%s / 수입거래] %s 계좌에 %s(으)로 %s원이 입금되었습니다.",
						journal.getTransactionDate().toString(),
						debit.getAccountName(),
						credit.getAccountName(),
						MoneyFormatter.moneyFormat(journal.getAmount())
				);
			}
			
			case EXPENSE : {
				return String.format("[%s / 지출거래] %s 계좌에서 %s(으)로 %s원이 지출되었습니다.",
						journal.getTransactionDate().toString(),
						credit.getAccountName(),
						debit.getAccountName(),
						MoneyFormatter.moneyFormat(journal.getAmount())
				);
			}
			
			case TRANSFER : {
				return String.format("[%s / 이체거래] %s 계좌에서 %s 계좌로 %s원이 이체되었습니다.",
						journal.getTransactionDate().toString(),
						credit.getAccountName(),
						debit.getAccountName(),
						MoneyFormatter.moneyFormat(journal.getAmount())
				);
			}
			
			default : throw new IllegalStateException("");
		}
	}
	
	public String updatedMessage(Journal journal) {
		Account debit = journal.getEntryLine(EntrySide.DEBIT).getAccount();
		Account credit = journal.getEntryLine(EntrySide.CREDIT).getAccount();
		
		switch (this) {
			case INCOME : {
				return String.format("[거래번호: %d / 수입거래] %s 계좌에 %s(으로) %s원이 입금된 내역으로 수정 완료되었습니다.",
						journal.getId(),
						debit.getAccountName(),
						credit.getAccountName(),
						MoneyFormatter.moneyFormat(journal.getAmount())
				);
			}
			
			case EXPENSE : {
				return String.format("[거래번호: %d / 지출거래] %s 계좌에서 %s(으)로 %s원이 지출된 내역으로 수정 완료되었습니다.",
						journal.getId(),
						credit.getAccountName(),
						debit.getAccountName(),
						MoneyFormatter.moneyFormat(journal.getAmount())
				);
			}
			
			case TRANSFER : {
				return String.format("[%s / 이체거래] %s 계좌에서 %s 계좌로 %s원이 이체된 내역으로 수정 완료되었습니다.",
						journal.getTransactionDate().toString(),
						credit.getAccountName(),
						debit.getAccountName(),
						MoneyFormatter.moneyFormat(journal.getAmount())
				);
			}
			
			default : throw new IllegalStateException("지원하지 않는 거래 유형입니다.");
		}
	}
	
}
