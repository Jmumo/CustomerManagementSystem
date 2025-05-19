package com.Jmumo.AccountService.repository;

import com.Jmumo.AccountService.domains.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaSpecificationExecutor<Account>,JpaRepository<Account, Long> {

    Optional<Account> findByCustomerId(Long customerId);

    Optional<Account> findByIban(String iban);

    Optional<Account> findByPublicId(UUID id);



    Page<Account> findByIbanContainingOrBicSwiftContaining(String iban, String bicSwift, Pageable pageable);


//
//        @Query("SELECT a FROM Account a " +
//                "WHERE (:iban IS NULL OR TRIM(LOWER(a.iban)) = TRIM(LOWER(:iban))) " +
//                "AND (:bicSwift IS NULL OR TRIM(LOWER(a.bicSwift)) = TRIM(LOWER(:bicSwift)))")
//        Page<Account> findByIbanAndBicSwift(
//                @Param("iban") String iban,
//                @Param("bicSwift") String bicSwift,
//                Pageable pageable);
//    }
}


