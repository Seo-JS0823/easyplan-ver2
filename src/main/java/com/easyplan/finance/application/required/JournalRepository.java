package com.easyplan.finance.application.required;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.easyplan.finance.domain.journal.Journal;
import com.easyplan.finance.domain.ledger.Ledger;

public interface JournalRepository extends JpaRepository<Journal, Long> {
	Optional<Journal> findByLedgerAndId(Ledger ledger, Long id);
	
	@Query("""
			SELECT j FROM Journal j
			JOIN FETCH j.entries e
			JOIN FETCH e.account a
			JOIN FETCH a.category c
			WHERE
					j.ledger = :ledger
					AND j.id = :id
	""")
	Optional<Journal> findByLedgerWithDetail(Ledger ledger, Long id);
}
