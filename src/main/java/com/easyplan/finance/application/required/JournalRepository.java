package com.easyplan.finance.application.required;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easyplan.finance.domain.journal.Journal;
import com.easyplan.finance.domain.ledger.Ledger;

public interface JournalRepository extends JpaRepository<Journal, Long> {
	Optional<Journal> findByLedgerAndId(Ledger ledger, Long id);
}
