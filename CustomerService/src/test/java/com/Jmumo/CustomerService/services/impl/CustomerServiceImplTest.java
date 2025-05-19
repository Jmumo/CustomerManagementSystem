package com.Jmumo.CustomerService.services.impl;

import com.Jmumo.CustomerService.Exceptions.CustomerNotFoundException;
import com.Jmumo.CustomerService.External.Dtos.AccountResponseDto;
import com.Jmumo.CustomerService.External.ExternalClient;
import com.Jmumo.CustomerService.Utils.CustomerMapper;
import com.Jmumo.CustomerService.domain.Customer;
import com.Jmumo.CustomerService.domain.Dtos.CustomerCreationDto;
import com.Jmumo.CustomerService.domain.Dtos.CustomerResponseDto;
import com.Jmumo.CustomerService.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private ExternalClient externalClient;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;
    private CustomerResponseDto customerResponseDto;
    private CustomerCreationDto customerCreationDto;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setCustomerId(1L);
        customer.setPublicId(UUID.randomUUID());
        customer.setFirstName("John");
        customer.setSecondName("Doe");

        customerResponseDto = new CustomerResponseDto();
        customerResponseDto.setPublicId(customer.getPublicId());
        customerResponseDto.setFirstName("John");
        customerResponseDto.setSecondName("Doe");

        customerCreationDto = new CustomerCreationDto();
        customerCreationDto.setFirstName("John");
        customerCreationDto.setSecondName("Doe");
    }

    @Test
    void createCustomer_Success() {
        when(customerMapper.toEntity(customerCreationDto)).thenReturn(customer);
        when(customerRepository.save(customer)).thenReturn(customer);
        when(customerMapper.toDto(customer)).thenReturn(customerResponseDto);

        Mono<CustomerResponseDto> result = customerService.createCustomer(customerCreationDto);

        StepVerifier.create(result)
                .expectNext(customerResponseDto)
                .verifyComplete();

        verify(customerMapper).toEntity(customerCreationDto);
        verify(customerRepository).save(customer);
        verify(customerMapper).toDto(customer);
    }

    @Test
    void searchCustomers_WithNameFilter_ShouldReturnFilteredResults() {
        // Given
        Page<Customer> customerPage = new PageImpl<>(List.of(customer));
        when(customerRepository.findAll((Specification<Customer>) any(), any(Pageable.class))).thenReturn(customerPage);
        when(customerMapper.toDto(customer)).thenReturn(customerResponseDto);
        when(externalClient.GetCustomerAccount(anyLong()))
                .thenReturn(Mono.just(new AccountResponseDto()));

        // When
        Mono<Page<CustomerResponseDto>> result = customerService.searchCustomers(
                "John", null, null, 0, 10);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(page ->
                        page.getContent().size() == 1 &&
                                page.getContent().get(0).getFirstName().equals("John"))
                .verifyComplete();
    }

    @Test
    void searchCustomers_WithDateFilter_ShouldReturnFilteredResults() {
        // Given
        Page<Customer> customerPage = new PageImpl<>(List.of(customer));
        when(customerRepository.findAll((Specification<Customer>) any(), any(Pageable.class))).thenReturn(customerPage);
        when(customerMapper.toDto(customer)).thenReturn(customerResponseDto);
        when(externalClient.GetCustomerAccount(anyLong()))
                .thenReturn(Mono.just(new AccountResponseDto()));

        // When
        Mono<Page<CustomerResponseDto>> result = customerService.searchCustomers(
                null, LocalDate.now(), LocalDate.now(), 0, 10);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(page -> !page.getContent().isEmpty())
                .verifyComplete();
    }

    @Test
    void searchCustomers_WhenAccountFetchFails_ShouldStillReturnCustomer() {
        // Given
        Page<Customer> customerPage = new PageImpl<>(List.of(customer));
        when(customerRepository.findAll((Specification<Customer>) any(), any(Pageable.class))).thenReturn(customerPage);
        when(customerMapper.toDto(customer)).thenReturn(customerResponseDto);
        when(externalClient.GetCustomerAccount(anyLong()))
                .thenReturn(Mono.error(new RuntimeException("Account service down")));

        // When
        Mono<Page<CustomerResponseDto>> result = customerService.searchCustomers(
                null, null, null, 0, 10);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(page ->
                        page.getContent().size() == 1 &&
                                page.getContent().get(0).getAccounts().isEmpty())
                .verifyComplete();
    }

    @Test
    void getCustomerById_Found_ShouldReturnCustomer() {
        // Given
        UUID publicId = customer.getPublicId();
        when(customerRepository.findByPublicId(publicId)).thenReturn(customer);

        // When
        Mono<Customer> result = customerService.getCustomerById(publicId);

        // Then
        StepVerifier.create(result)
                .expectNext(customer)
                .verifyComplete();
    }

    @Test
    void getCustomerById_NotFound_ShouldThrowException() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(customerRepository.findByPublicId(nonExistentId)).thenReturn(null);

        // When
        Mono<Customer> result = customerService.getCustomerById(nonExistentId);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(ex ->
                        ex instanceof CustomerNotFoundException &&
                                ex.getMessage().equals("Customer not found with public ID: " + nonExistentId))
                .verify();
    }

    @Test
    void deleteCustomer_ShouldCallRepository() {
        // Given
        Long customerId = 1L;
        doNothing().when(customerRepository).deleteById(customerId);

        // When
        Mono<Void> result = customerService.deleteCustomer(customerId);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(customerRepository).deleteById(customerId);
    }

    @Test
    void updateCustomer_Success() {
        // Given
        Long customerId = 1L;
        Customer updatedCustomer = new Customer();
        updatedCustomer.setFirstName("Updated");
        updatedCustomer.setSecondName("Name");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any())).thenReturn(updatedCustomer);

        // When
        Mono<Customer> result = customerService.updateCustomer(customerId, updatedCustomer);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(c ->
                        c.getFirstName().equals("Updated") &&
                                c.getSecondName().equals("Name"))
                .verifyComplete();
    }

    @Test
    void updateCustomer_NotFound_ShouldReturnEmpty() {
        // Given
        Long nonExistentId = 999L;
        when(customerRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When
        Mono<Customer> result = customerService.updateCustomer(nonExistentId, new Customer());

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }
}