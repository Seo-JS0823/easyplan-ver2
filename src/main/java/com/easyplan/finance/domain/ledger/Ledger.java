package com.easyplan.finance.domain.ledger;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;

import com.easyplan._global.infra.jpa.BaseEntity;
import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.domain.ledger.exception.LedgerException;
import com.easyplan.finance.domain.ledger.exception.LedgerExceptionCode;
import com.easyplan.finance.domain.ledger.request.LedgerCreateRequest;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
		name = "ledger",
		uniqueConstraints = {
				@UniqueConstraint(
						name = "uk_owner_id_ledger_name",
						columnNames = {"owner_id", "ledger_name"}
				)
		}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@NaturalIdCache
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Ledger extends BaseEntity {
	
	// Member -> id
	@Column(name = "owner_id", nullable = false, updatable = false)
	private Long ownerId;
	
	@NaturalId
	@Embedded
	@AttributeOverride(
			name = "publicId",
			column = @Column(name = "ledger_public_id", nullable = false, unique = true, updatable = false, length = 40)
	)
	private PublicId ledgerPublicId;
	
	@Column(name = "ledger_type", nullable = false, updatable = false, length = 10)
	@Enumerated(EnumType.STRING)
	private LedgerType type;
	
	@Column(name = "ledger_name", nullable = false, length = 20)
	private String name;
	
	@Column(name = "ledger_description", length = 300)
	private String description;
	
	@Column(name = "ledger_status", nullable = false, length = 10)
	private LedgerStatus status;
	
	@Embedded
	@AttributeOverride(
			name = "day",
			column = @Column(name = "fiscal_start_day", nullable = false)
	)
	private FiscalStartDay fiscalStartDay;
	
	public static Ledger create(Long ownerId, LedgerCreateRequest ledgerCreate) {
		Ledger ledger = new Ledger();
		
		ledger.ownerId = ownerId;
		ledger.type = ledgerCreate.type();
		ledger.name = ledgerCreate.name();
		ledger.description = ledgerCreate.description();
		
		ledger.ledgerPublicId = PublicId.create();
		ledger.status = LedgerStatus.ACTIVE;
		ledger.fiscalStartDay = new FiscalStartDay(1);
		
		return ledger;
	}
	
	public void changeInfo(String name, String description) {
		isActive();
		
		this.name = name;
		this.description = description;
	}
	
	public void changeFiscalDay(int fiscalStartDay) {
		isActive();
		
		this.fiscalStartDay = new FiscalStartDay(fiscalStartDay);
	}
	
	public void archived() {
		if(this.status == LedgerStatus.ARCHIVED) {
			return;
		}
		
		this.status = LedgerStatus.ARCHIVED;
	}
	
	public void reactivate() {
		if(this.status == LedgerStatus.ACTIVE) {
			return;
		}
		
		this.status = LedgerStatus.ACTIVE;
	}
	
	public void isActive() {
		if(this.status != LedgerStatus.ACTIVE) {
			throw new LedgerException(LedgerExceptionCode.LEDGER_NOT_ACTIVE);
		}
	}
}
