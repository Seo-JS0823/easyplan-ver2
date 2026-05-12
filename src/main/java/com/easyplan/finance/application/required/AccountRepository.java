package com.easyplan.finance.application.required;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.domain.account.Account;
import com.easyplan.finance.domain.account.AccountStatus;
import com.easyplan.finance.domain.account.Category;
import com.easyplan.finance.domain.ledger.Ledger;

public interface AccountRepository extends JpaRepository<Account, Long> {
	List<Account> findByLedger(Ledger ledger);
	
	Optional<Account> findByLedgerAndAccountPublicId(Ledger ledger, PublicId accountPublicId);
	
	@Query("""
			SELECT a FROM Account a
			JOIN FETCH a.category
			WHERE
					a.ledger = :ledger
					AND a.accountPublicId = :accountPublicId
					AND a.status = :status
	""")
	Optional<Account> findByLedgerAndAccountPublicIdAndStatus(Ledger ledger, PublicId accountPublicId, AccountStatus status);
	
	boolean existsByCategoryAndAccountName(Category category, String accountName);
	
	boolean existsByCategoryAndAccountNameAndIdNot(Category category, String accountName, Long id);
	
	@Query("""
			SELECT a FROM Account a
			JOIN FETCH a.category
			WHERE
					a.ledger = :ledger
					AND a.accountPublicId IN :accountPublicId
	""")
	List<Account> findByLedgerAndAccountPublicIdInWithCategory(Ledger ledger, List<PublicId> accountPublicId);

	Optional<Account> findByLedgerAndCategory(Ledger ledger, Category category);
}
