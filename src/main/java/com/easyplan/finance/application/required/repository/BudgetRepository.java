package com.easyplan.finance.application.required.repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.easyplan.finance.domain.account.Account;
import com.easyplan.finance.domain.budget.Budget;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
	Optional<Budget> findByAccountAndPeriod(Account account, YearMonth period);
	
	@Query("""
			SELECT b FROM Budget b
			JOIN FETCH b.account a
			WHERE
					a IN :accounts
					AND b.period = :period
	""")
	List<Budget> findBudgetList(List<Account> accounts, YearMonth period);
}
