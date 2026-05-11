package com.easyplan.finance.application.required;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easyplan.finance.domain.journal.EntryLine;

public interface EntryLineRepository extends JpaRepository<EntryLine, Long> {

}
