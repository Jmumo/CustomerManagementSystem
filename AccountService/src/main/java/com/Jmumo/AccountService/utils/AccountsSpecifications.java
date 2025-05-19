package com.Jmumo.AccountService.utils;

import com.Jmumo.AccountService.domains.Account;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;


@Slf4j
public class AccountsSpecifications {

    public static Specification<Account> hasIban(String iban) {
        return (root, query, criteriaBuilder) -> {
            log.info("Filtering by IBAN: {}", iban);
            return criteriaBuilder.equal(root.get("iban"), iban);
        };
    }

    public static Specification<Account> hasBicSwift(String bicSwift) {
        return (root, query, criteriaBuilder) -> {
            log.info("Filtering by BIC/Swift: {}", bicSwift);
            return criteriaBuilder.equal(root.get("bicSwift"), bicSwift);
        };
    }
}


