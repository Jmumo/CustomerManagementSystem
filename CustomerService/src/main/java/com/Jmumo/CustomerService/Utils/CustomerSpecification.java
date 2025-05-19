package com.Jmumo.CustomerService.Utils;

import com.Jmumo.CustomerService.domain.Customer;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class CustomerSpecification {

    public static Specification<Customer> nameContains(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + name.toLowerCase() + "%"),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("secondName")), "%" + name.toLowerCase() + "%"),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("otherNames")), "%" + name.toLowerCase() + "%")
        );
    }

    public static Specification<Customer> createdDateBetween(LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("createdDate"), startDate, endDate);
    }
}
