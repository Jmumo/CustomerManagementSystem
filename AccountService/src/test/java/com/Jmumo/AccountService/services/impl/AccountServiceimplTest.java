package com.Jmumo.AccountService.services.impl;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


import com.Jmumo.AccountService.domains.Account;
import com.Jmumo.AccountService.domains.Dtos.AccountCreationDto;
import com.Jmumo.AccountService.domains.Dtos.AccountResponseDto;
import com.Jmumo.AccountService.domains.Dtos.CardDto;
import com.Jmumo.AccountService.external.Dtos.CustomerResponseDto;
import com.Jmumo.AccountService.external.ExternalClient;
import com.Jmumo.AccountService.repository.AccountRepository;
import com.Jmumo.AccountService.utils.AccountMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.*;

import static org.mockito.Mockito.*;

@Nested
@ExtendWith(MockitoExtension.class)
class AccountServiceimplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private ExternalClient externalClient;

    @InjectMocks
    private AccountServiceimpl accountService;

    private AccountCreationDto accountCreationDto;
    private AccountResponseDto accountResponseDto;
    private Account account;
    private CustomerResponseDto customerDto;

    @BeforeEach
    void setUp() {
        UUID customerId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        accountCreationDto = new AccountCreationDto();
        accountCreationDto.setIban("IBAN123");
        accountCreationDto.setCustomerId(customerId);
        accountCreationDto.setBicSwift("BIC123");

        accountResponseDto = new AccountResponseDto();
        accountResponseDto.setIban("IBAN123");
        accountResponseDto.setCustomerId(1L);
        accountResponseDto.setBicSwift("BIC123");

        account = new Account();
        account.setIban("IBAN123");
        account.setCustomerId(1L);
        account.setBicSwift("BIC123");
        account.setAccountId(1L);

        customerDto = new CustomerResponseDto();
        customerDto.setCustomerId(1L);
        customerDto.setFirstName("John");
        customerDto.setSecondName("Doe");
    }

    @Test
    void createAccount_Success() {
        // Arrange
        UUID customerId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        when(externalClient.GetCustomerAccount(customerId)).thenReturn(Mono.just(customerDto));
        when(accountRepository.findByIban(anyString())).thenReturn(Optional.empty());
        when(accountMapper.toEntity(any(AccountCreationDto.class))).thenReturn(account);
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(accountMapper.toDto(any(Account.class))).thenReturn(accountResponseDto);

        // Act & Assert
        StepVerifier.create(accountService.createAccount(accountCreationDto))
                .expectNext(accountResponseDto)
                .verifyComplete();

        verify(externalClient).GetCustomerAccount(any());
        verify(accountRepository).findByIban("IBAN123");
        verify(accountRepository).save(account);
    }



    @Test
    void createAccount_CustomerNotFound() {
        // Arrange
        UUID customerId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        AccountCreationDto accountCreationDto = new AccountCreationDto();
        accountCreationDto.setIban("IBAN123");
        accountCreationDto.setCustomerId(customerId);

        when(externalClient.GetCustomerAccount(customerId)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(accountService.createAccount(accountCreationDto))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Customer with ID 123e4567-e89b-12d3-a456-426614174000 does not exist."))
                .verify();

        verify(externalClient).GetCustomerAccount(customerId);
        verify(accountRepository, never()).findByIban(anyString());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void createAccount_AccountAlreadyExists() {
        // Arrange
        UUID customerId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        when(externalClient.GetCustomerAccount(customerId)).thenReturn(Mono.just(customerDto));
        when(accountRepository.findByIban(anyString())).thenReturn(Optional.of(account));

        // Act & Assert
        StepVerifier.create(accountService.createAccount(accountCreationDto))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Account with this IBAN already exists."))
                .verify();

        verify(externalClient).GetCustomerAccount(any());
        verify(accountRepository).findByIban("IBAN123");
        verify(accountRepository, never()).save(any());
    }

    @Test
    void getAllAccounts_Success() {
        // Arrange
        List<Account> accounts = List.of(account);
        when(accountRepository.findAll()).thenReturn(accounts);

        // Act & Assert
        StepVerifier.create(accountService.getAllAccounts())
                .expectNext(accounts)
                .verifyComplete();

        verify(accountRepository).findAll();
    }



    @Test
    void getAccountById_Success() {
        // Arrange
        UUID accountId = UUID.randomUUID();
        when(accountRepository.findByPublicId(accountId)).thenReturn(Optional.of(account));

        // Act & Assert
        StepVerifier.create(accountService.getAccountById(accountId))
                .expectNext(account)
                .verifyComplete();

        verify(accountRepository).findByPublicId(accountId);
    }

    @Test
    void getAccountById_NotFound() {
        // Arrange
        UUID accountId = UUID.randomUUID();
        when(accountRepository.findByPublicId(accountId)).thenReturn(Optional.empty());

        // Act & Assert
        StepVerifier.create(accountService.getAccountById(accountId))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Account not found"))
                .verify();

        verify(accountRepository).findByPublicId(accountId);
    }

    @Test
    void updateAccount_Success() {
        // Arrange
        Long customerId = 1L;
        Account updatedAccount = new Account();
        updatedAccount.setBicSwift("NEWBIC");
        updatedAccount.setIban("NEWIBAN");

        when(accountRepository.findByCustomerId(customerId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(updatedAccount);

        // Act & Assert
        StepVerifier.create(accountService.updateAccount(customerId, updatedAccount))
                .expectNext(updatedAccount)
                .verifyComplete();

        verify(accountRepository).findByCustomerId(customerId);
        verify(accountRepository).save(account);
    }

    @Test
    void updateAccount_NotFound() {
        // Arrange
        Long customerId = 1L;
        Account updatedAccount = new Account();
        when(accountRepository.findByCustomerId(customerId)).thenReturn(Optional.empty());

        // Act & Assert
        StepVerifier.create(accountService.updateAccount(customerId, updatedAccount))
                .expectNextCount(0)
                .verifyComplete();

        verify(accountRepository).findByCustomerId(customerId);
        verify(accountRepository, never()).save(any());
    }

    @Test
    void getAllAccountsWithFilters_Success() {
        // Arrange
        String iban = "IBAN123";
        String bicSwift = "BIC123";
        String cardAlias = "Personal";
        int page = 0;
        int size = 10;

        Pageable pageable = PageRequest.of(page, size);
        List<Account> accounts = List.of(account);
        Page<Account> accountPage = new PageImpl<>(accounts, pageable, accounts.size());

        CardDto cardDto = new CardDto();
        cardDto.setCardAlias("Personal");
        List<CardDto> cards = List.of(cardDto);

        AccountResponseDto responseDto = new AccountResponseDto();
        responseDto.setIban(iban);
        responseDto.setCards(cards);

        // Mocking repository and external client responses
        when(accountRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(accountPage);
        when(externalClient.fetchCardsForAccount(any(Account.class)))
                .thenReturn(Mono.just(cards));
        when(accountMapper.toDto(any(Account.class)))
                .thenReturn(responseDto);

        // Act & Assert
        StepVerifier.create(accountService.getAllAccounts(iban, bicSwift, page, size, cardAlias))
                .assertNext(pageResult -> {
                    assertThat(pageResult).isNotNull();
                    assertThat(pageResult.getContent()).hasSize(1);  // Check that the page has one account
                    AccountResponseDto dto = pageResult.getContent().get(0);
                    assertThat(dto.getIban()).isEqualTo("IBAN123");
                    assertThat(dto.getCards()).hasSize(1);
                    assertThat(dto.getCards().get(0).getCardAlias()).isEqualTo("Personal");
                })
                .verifyComplete();

        // Verify interactions
        verify(accountRepository).findAll(any(Specification.class), any(Pageable.class));
        verify(externalClient).fetchCardsForAccount(account);
    }



    @Test
    void getAccountByCustomer_Success() {
        // Arrange
        Long customerId = 1L;
        when(accountRepository.findByCustomerId(customerId)).thenReturn(Optional.of(account));

        // Act & Assert
        StepVerifier.create(accountService.getAccountByCustomer(customerId))
                .expectNext(account)
                .verifyComplete();

        verify(accountRepository).findByCustomerId(customerId);
    }

    @Test
    void getAccountByCustomer_NotFound() {
        // Arrange
        Long customerId = 1L;
        when(accountRepository.findByCustomerId(customerId)).thenReturn(Optional.empty());

        // Act & Assert
        StepVerifier.create(accountService.getAccountByCustomer(customerId))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Account not found"))
                .verify();

        verify(accountRepository).findByCustomerId(customerId);
    }
}
