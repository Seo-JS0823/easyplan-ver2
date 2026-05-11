package com.easyplan.finance.application.required;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.domain.ledger.Ledger;

public interface LedgerRepository extends JpaRepository<Ledger, Long> {
	Optional<Ledger> findByLedgerPublicId(PublicId ledgerPublicId);
	
	boolean existsByLedgerMemberIdAndLedgerName(Long ledgerMemberId, String ledgerName);
	
	boolean existsByLedgerMemberIdAndLedgerNameAndIdNot(Long ledgerMemberId, String ledgerName, Long id);
}
