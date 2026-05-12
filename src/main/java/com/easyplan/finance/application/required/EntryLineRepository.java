package com.easyplan.finance.application.required;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easyplan.finance.domain.journal.EntryLine;
import com.easyplan.finance.domain.journal.Journal;

public interface EntryLineRepository extends JpaRepository<EntryLine, Long> {
	List<EntryLine> findByJournal(Journal journal);
}
