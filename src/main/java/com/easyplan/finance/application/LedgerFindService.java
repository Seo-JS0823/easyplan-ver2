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
import com.easyplan.finance.domain.ledger.exception.LedgerException;
import com.easyplan.finance.domain.ledger.exception.LedgerExceptionCode;
import com.easyplan.member.application.provided.MemberPolicy;
import com.easyplan.member.application.provided.MemberSummary;

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
	
	private final MemberPolicy memberPolicy;
	
	@Override
	public List<Ledger> findByOwnerId(Long ownerId) {
		return ledgerRepo.findByOwnerId(ownerId);
	}

	@Override
	public Ledger findByLedgerPublicId(PublicId memberPublicId, PublicId ledgerPublicId) {
		MemberSummary member = memberPolicy.canUseService(memberPublicId);
		
		Ledger ledger = em.unwrap(Session.class)
				.bySimpleNaturalId(Ledger.class)
				.load(ledgerPublicId);
		
		if(ledger == null || !ledger.getOwnerId().equals(member.getId())) {
			throw new LedgerException(LedgerExceptionCode.LEDGER_NOT_FOUND);
		}
		
		return ledger;
	}

	@Override
	public List<Ledger> findMyLedgers(PublicId memberPublicId) {
		Long ownerId = memberPolicy.canUseService(memberPublicId).getId();
		
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

	@Override
	public boolean existsByOwnerIdAndLedgerPublicId(Long ownerId, PublicId ledgerPublicId) {
		return ledgerRepo.existsByOwnerIdAndLedgerPublicId(ownerId, ledgerPublicId);
	}

	@Override
	public Optional<Ledger> findByid(Long ledgerId) {
		return ledgerRepo.findById(ledgerId);
	}

	@Override
	public Optional<Ledger> findByOwnerIdAndLedgerPublicId(Long id, PublicId ledgerPublicId) {
		return ledgerRepo.findByOwnerIdAndLedgerPublicId(id, ledgerPublicId);
	}

}
