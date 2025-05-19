package com.Jmumo.CustomerService.services;

import com.Jmumo.CustomerService.domain.Customer;
import com.Jmumo.CustomerService.domain.Dtos.CustomerCreationDto;
import com.Jmumo.CustomerService.domain.Dtos.CustomerResponseDto;
import org.springframework.data.domain.Page;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface CustomerService {
    Mono<CustomerResponseDto> createCustomer(CustomerCreationDto customerCreationDto);

    Mono<Page<CustomerResponseDto>> searchCustomers(
            String name, LocalDate startDate, LocalDate endDate, int page, int size
    );

    Mono<Customer> getCustomerById(UUID id);

    Mono<Void> deleteCustomer(Long id);

    Mono<Customer> updateCustomer(Long id, Customer customer);
}
