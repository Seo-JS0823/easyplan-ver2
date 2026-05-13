package com.easyplan.finance.domain.ledger;

import java.time.LocalDate;
import java.time.YearMonth;

import com.easyplan._shared.domain.BaseEntity;
import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.domain.ledger.exception.LedgerErrorCode;
import com.easyplan.finance.domain.ledger.exception.LedgerException;
import com.easyplan.finance.domain.ledger.request.LedgerCreateRequest;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ledger")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ledger extends BaseEntity {
	
	@Column(name = "member_id", nullable = false, updatable = false)
	private Long ledgerMemberId;
	
	@Embedded
	@AttributeOverride(
			name = "publicId",
			column = @Column(name = "ledger_public_id", nullable = false, unique = true, updatable = false)
	)
	private PublicId ledgerPublicId;
	
	@Column(name = "ledger_type", nullable = false, updatable = false, length = 10)
	@Enumerated(EnumType.STRING)
	private LedgerType ledgerType;
	
	@Column(name = "ledger_name", nullable = false, length = 20)
	private String ledgerName;
	
	@Column(name = "ledger_description", nullable = false, length = 300)
	private String ledgerDescription;
	
	@Embedded
	@AttributeOverride(
			name = "day",
			column = @Column(name = "fiscal_day", nullable = false)
	)
	private FiscalDay fiscalDay;
	
	public static Ledger create(Long memberId, LedgerCreateRequest ledgerCreate) {
		Ledger ledger = new Ledger();
		
		ledger.ledgerPublicId = PublicId.create();
		
		ledger.ledgerMemberId = memberId;
		ledger.ledgerType = ledgerCreate.type();
		ledger.ledgerName = ledgerCreate.name();
		ledger.ledgerDescription = ledgerCreate.description();
		
		ledger.fiscalDay = new FiscalDay(1);
		
		return ledger;
	}
	
	public void changeInfo(String ledgerName, String ledgerDescription) {
		this.ledgerName = ledgerName;
		this.ledgerDescription = ledgerDescription;
	}
	
	public void changeFiscalDay(int fiscalDay) {
		this.fiscalDay = new FiscalDay(fiscalDay);
	}
	
	public LocalDate fiscalStartDate(YearMonth month) {
		return getFiscalDay().resolveStartDate(month);
	}
	
	public LocalDate fiscalEndDate(YearMonth month) {
		return getFiscalDay().resolveEndDate(month);
	}
	
	/*
	 * LedgerPolicyService가 하던 이 가계부 니꺼냐? 라는 DB IO 조회 로직 등이 포함된 동작을
	 * 파라미터로 memberId 받고 비교하면 끝인걸 왜 어렵게했을까 ㅠㅠ
	 * 
	 * 원래 void 였는데 자기자신 반환하게 해서 체이닝 형식으로 하면 더 좋을듯
	 */
	public Ledger validateOwner(Long memberId) {
		if(!this.ledgerMemberId.equals(memberId)) {
			throw new LedgerException(LedgerErrorCode.LEDGER_NOT_FOUND);
		}
		
		return this;
	}
	
}
