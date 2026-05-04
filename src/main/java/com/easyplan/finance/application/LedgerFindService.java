package com.easyplan.finance.application;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.application.provided.ledger.LedgerFinder;
import com.easyplan.finance.application.required.LedgerRepository;
import com.easyplan.finance.domain.ledger.Ledger;
import com.easyplan.member.application.provided.MemberFinder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LedgerFindService implements LedgerFinder {
	
	@PersistenceContext
	private EntityManager em;
	
	private final LedgerRepository ledgerRepo;
	
	private final MemberFinder memberFinder;
	
	@Override
	public List<Ledger> findByOwnerId(Long ownerId) {
		return ledgerRepo.findByOwnerId(ownerId);
	}

	@Override
	public Ledger findByLedgerPublicId(PublicId ledgerPublicId) {
		// return ledgerRepo.findByLedgerPublicId(ledgerPublicId);
		
		return em.unwrap(Session.class)
				.bySimpleNaturalId(Ledger.class)
				.load(ledgerPublicId);
	}

	@Override
	public List<Ledger> findMyLedgers(PublicId memberPublicId) {
		Long ownerId = memberFinder.findByPublicId(memberPublicId).getId();
		
		return findByOwnerId(ownerId);
	}

	@Override
	public int countByOwnerId(Long ownerId) {
		return ledgerRepo.countByOwnerId(ownerId);
	}

	@Override
	public Optional<Ledger> findByLedgerPublicIdAndOwnerId(PublicId ledgerPublicId, Long ownerId) {
		return ledgerRepo.findByLedgerPublicIdAndOwnerId(ledgerPublicId, ownerId);
	}

	@Override
	public boolean existsByOwnerIdAndName(Long ownerId, String name) {
		return ledgerRepo.existsByOwnerIdAndName(ownerId, name);
	}

}
