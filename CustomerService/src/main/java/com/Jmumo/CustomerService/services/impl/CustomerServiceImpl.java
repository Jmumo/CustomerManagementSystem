package com.Jmumo.CustomerService.services.impl;


import com.Jmumo.CustomerService.Exceptions.CustomerNotFoundException;
import com.Jmumo.CustomerService.External.Dtos.AccountResponseDto;
import com.Jmumo.CustomerService.External.ExternalClient;
import com.Jmumo.CustomerService.Utils.CustomerMapper;
import com.Jmumo.CustomerService.Utils.CustomerSpecification;
import com.Jmumo.CustomerService.domain.Customer;
import com.Jmumo.CustomerService.domain.Dtos.CustomerCreationDto;
import com.Jmumo.CustomerService.domain.Dtos.CustomerResponseDto;
import com.Jmumo.CustomerService.repositories.CustomerRepository;
import com.Jmumo.CustomerService.services.CustomerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final ExternalClient externalClient;

    @Override
    public Mono<CustomerResponseDto> createCustomer(CustomerCreationDto customerCreationDto) {
        return Mono.fromCallable(()->{
            Customer customer = customerMapper.toEntity(customerCreationDto);
            return customerRepository.save(customer);
                })
                .map(customerMapper::toDto);
    }


    @Override
    public Mono<Page<CustomerResponseDto>> searchCustomers(String name, LocalDate startDate, LocalDate endDate, int page, int size) {
        List<Specification<Customer>> specs = new ArrayList<>();

        if (name != null) {
            specs.add(CustomerSpecification.nameContains(name));
        }
        if (startDate != null && endDate != null) {
            specs.add(CustomerSpecification.createdDateBetween(startDate, endDate));
        }

        Specification<Customer> combinedSpec = specs.stream()
                .reduce(Specification::and)
                .orElse((root, query, criteriaBuilder) -> criteriaBuilder.conjunction());

        Pageable pageable = PageRequest.of(page, size);

        return Mono.fromCallable(() -> customerRepository.findAll(combinedSpec, pageable))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(customerPage -> Flux.fromIterable(customerPage.getContent())
                        .flatMap(customer -> {
                            // Create base DTO without account first
                            CustomerResponseDto customerDto = customerMapper.toDto(customer);
                            customerDto.setAccounts(new ArrayList<>()); // Initialize empty accounts list

                            // Try to fetch account, but always return the customer
                            return externalClient.GetCustomerAccount(customer.getCustomerId())
                                    .map(account -> {
                                        customerDto.setAccounts(Collections.singletonList(account));
                                        log.info("Fetched account for customer ID {}: {}", customer.getCustomerId(), account);
                                        return customerDto;
                                    })
                                    .onErrorResume(error -> {
                                        log.warn("No account found for customer ID {}: {}", customer.getCustomerId(), error.getMessage());
                                        return Mono.just(customerDto); // Return customer with empty accounts list
                                    })
                                    .defaultIfEmpty(customerDto); // Handle case where GetCustomerAccount returns empty Mono
                        })
                        .collectList()
                        .map(customerList -> (Page<CustomerResponseDto>) new PageImpl<>(customerList, pageable, customerPage.getTotalElements()))
                )
                .doOnSuccess(customers ->
                        log.info("Retrieved {} customers ({} with accounts)",
                                customers.getContent().size(),
                                customers.getContent().stream().filter(c -> !c.getAccounts().isEmpty()).count())
                )
                .doOnError(error ->
                        log.error("Error retrieving customers: {}", error.getMessage())
                );
    }



    @Override
    public Mono<Customer> getCustomerById(UUID id) {
        return Mono.fromCallable(()->customerRepository.findByPublicId(id))
                .switchIfEmpty(Mono.error(new CustomerNotFoundException("Customer not found with public ID: " + id)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> deleteCustomer(Long id) {
        return Mono.fromRunnable(()->customerRepository.deleteById(id));
    }


    @Override
    public Mono<Customer> updateCustomer(Long id, Customer updatedCustomer) {
        return Mono.fromCallable(() -> customerRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optionalCustomer -> optionalCustomer.map(existingCustomer -> {
                    existingCustomer.setFirstName(updatedCustomer.getFirstName());
                    existingCustomer.setSecondName(updatedCustomer.getSecondName());
                    existingCustomer.setOtherNames(updatedCustomer.getOtherNames());
                    return Mono.fromCallable(() -> customerRepository.save(existingCustomer))
                            .subscribeOn(Schedulers.boundedElastic());
                }).orElse(Mono.empty()));
    }
}
