package com.Jmumo.CustomerService.repositories;

import com.Jmumo.CustomerService.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface CustomerRepository extends JpaSpecificationExecutor<Customer>,JpaRepository<Customer, Long> {

    Customer findByPublicId(UUID id);
}
