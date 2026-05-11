package com.easyplan.finance.application.required;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easyplan.finance.domain.journal.Journal;

public interface JournalRepository extends JpaRepository<Journal, Long> {

}
