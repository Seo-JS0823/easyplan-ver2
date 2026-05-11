package com.easyplan.finance.application.required;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easyplan.finance.domain.account.AccountType;
import com.easyplan.finance.domain.account.Category;
import com.easyplan.finance.domain.ledger.Ledger;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	Optional<Category> findByLedgerAndAccountType(Ledger ledger, AccountType accountType);
}
