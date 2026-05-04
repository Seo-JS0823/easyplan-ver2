package com.easyplan.finance.application.required;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.domain.ledger.Ledger;

public interface LedgerRepository extends JpaRepository<Ledger, Long> {
	List<Ledger> findByOwnerId(Long ownerId);
	
	boolean existsByOwnerIdAndName(Long ownerId, String name);
	
	Ledger findByLedgerPublicId(PublicId ledgerPublicId);
	
	Optional<Ledger> findByLedgerPublicIdAndOwnerId(PublicId ledgerPublicId, Long ownerId);
	
	int countByOwnerId(Long ownerId);
}
