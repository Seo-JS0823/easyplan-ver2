package com.easyplan.finance.application.provided.ledger;

import java.util.List;
import java.util.Optional;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.domain.ledger.Ledger;

public interface LedgerFinder {
	List<Ledger> findByOwnerId(Long ownerId);
	
	Optional<Ledger> findByid(Long ledgerId);
	
	boolean existsByOwnerIdAndName(Long ownerId, String name);
	
	boolean existsByOwnerIdAndLedgerPublicId(Long ownerId, PublicId ledgerPublicId);
	
	List<Ledger> findMyLedgers(PublicId memberPublicId);
	
	Ledger findByLedgerPublicId(PublicId memberPublicId, PublicId ledgerPublicId);
	
	Optional<Ledger> findByLedgerPublicIdAndOwnerId(PublicId ledgerPublicId, Long ownerId);
	
	int countByOwnerId(Long ownerId);

	Optional<Ledger> findByOwnerIdAndLedgerPublicId(Long id, PublicId ledgerPublicId);
}
