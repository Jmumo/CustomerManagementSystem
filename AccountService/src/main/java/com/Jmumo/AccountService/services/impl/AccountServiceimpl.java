package com.Jmumo.AccountService.services.impl;


import com.Jmumo.AccountService.domains.Account;
import com.Jmumo.AccountService.domains.Dtos.AccountCreationDto;
import com.Jmumo.AccountService.domains.Dtos.AccountResponseDto;
import com.Jmumo.AccountService.domains.Dtos.CardDto;
import com.Jmumo.AccountService.external.ExternalClient;
import com.Jmumo.AccountService.repository.AccountRepository;
import com.Jmumo.AccountService.services.AccountService;
import com.Jmumo.AccountService.utils.AccountMapper;
import com.Jmumo.AccountService.utils.AccountsSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceimpl implements AccountService {

    private final AccountRepository accountRepository;
    private final Scheduler jdbcScheduler = Schedulers.boundedElastic();
    private final AccountMapper accountMapper;
    private final ExternalClient externalClient;




    @Override
    public Mono<AccountResponseDto> createAccount(AccountCreationDto accountCreationDto) {
        log.info("Starting account creation for IBAN: {} and Customer ID: {}",
                accountCreationDto.getIban(), accountCreationDto.getCustomerId());

        return externalClient.GetCustomerAccount(accountCreationDto.getCustomerId())   // Step 1: Call customer service
                .doOnNext(customer ->
                        log.info("Customer found: ID={}, Name={}",
                                customer.getCustomerId(), customer.getFirstName() + " " + customer.getSecondName()))
                .switchIfEmpty(Mono.error(new IllegalArgumentException(
                        "Customer with ID " + accountCreationDto.getCustomerId() + " does not exist.")))
                .doOnError(error ->
                        log.error("Customer verification failed: {}", error.getMessage()))
                .flatMap(customer -> Mono.fromCallable(() -> accountRepository.findByIban(accountCreationDto.getIban()))
                        .doOnNext(optionalAccount -> {
                            if (optionalAccount.isPresent()) {
                                log.warn("Account with IBAN {} already exists.", accountCreationDto.getIban());
                            }
                        })
                        .flatMap(optionalAccount -> {
                            if (optionalAccount.isPresent()) {
                                return Mono.error(new IllegalArgumentException("Account with this IBAN already exists."));
                            }
                            Account account = accountMapper.toEntity(accountCreationDto);

                            // Set the customer ID from the fetched customer object
                            account.setCustomerId(customer.getCustomerId());
                            log.info("Setting customer ID from fetched customer: {}", customer.getCustomerId());

                            log.info("Creating new account with IBAN: {}", account.getIban());

                            return Mono.fromCallable(() -> accountRepository.save(account))
                                    .doOnSuccess(savedAccount ->
                                            log.info("Successfully created account with ID: {}", savedAccount.getAccountId()))
                                    .map(accountMapper::toDto)
                                    .doOnError(error ->
                                            log.error("Failed to create account: {}", error.getMessage()));
                        }))
                .doOnError(error ->
                        log.error("Account creation process failed: {}", error.getMessage()))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<List<Account>> getAllAccounts() {
        return Mono.fromCallable(accountRepository::findAll);
    }

    @Override
    public Mono<Account> getAccountById(UUID id) {
        return Mono.fromCallable(() -> accountRepository.findByPublicId(id))
                .flatMap(optionalAccount -> optionalAccount
                        .map(account -> {
                            log.info("Fetched account: {}", account);
                            return Mono.just(account);
                        })
                        .orElseGet(() -> {
                            log.warn("Account not found with ID: {}", id);
                            return Mono.error(new IllegalArgumentException("Account not found"));
                        })
                );
    }

    @Override
    public Mono<Account> updateAccount(Long id, Account updatedAccount) {
        return Mono.fromCallable(() -> {
            Optional<Account> existingAccountOptional = accountRepository.findByCustomerId(id);
            if (existingAccountOptional.isPresent()) {
                Account existingAccount = existingAccountOptional.get();

                    existingAccount.setBicSwift(updatedAccount.getBicSwift());
                    existingAccount.setIban(updatedAccount.getIban());

                return accountRepository.save(existingAccount);
            } else {
                return null;
            }
        });
    }



    @Override
    public Mono<Void> deleteAccount(Long id) {
        return Mono.fromRunnable(() -> accountRepository.deleteById(id));
    }



    @Override
    public Mono<Page<AccountResponseDto>> getAllAccounts(
            String iban, String bicSwift, int page, int size, String cardAlias) {

        List<Specification<Account>> specs = new ArrayList<>();

        if (iban != null) {
            specs.add(AccountsSpecifications.hasIban(iban));
        }
        if (bicSwift != null) {
            specs.add(AccountsSpecifications.hasBicSwift(bicSwift));
        }

        Specification<Account> combinedSpec = specs.stream()
                .reduce(Specification::and)
                .orElse((root, query, criteriaBuilder) -> criteriaBuilder.conjunction());

        Pageable pageable = PageRequest.of(page, size);

        return Mono.fromCallable(() -> accountRepository.findAll(combinedSpec, pageable))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(accountPage -> Flux.fromIterable(accountPage.getContent())
                        .flatMap(account -> externalClient.fetchCardsForAccount(account)
                                .map(cards -> {

                                    List<CardDto> filteredCards = cards.stream()
                                            .filter(card -> cardAlias == null ||
                                                    (card.getCardAlias() != null && card.getCardAlias().equals(cardAlias.trim())))
                                            .collect(Collectors.toList());

                                    if (!filteredCards.isEmpty() || cardAlias == null) {
                                        AccountResponseDto dto = accountMapper.toDto(account);
                                        dto.setCards(filteredCards);
                                        return dto;
                                    }
                                    return null;
                                })
                                .onErrorResume(error -> {
                                    log.warn("Error fetching cards for account ID {}: {}", account.getAccountId(), error.getMessage());
                                    return Mono.empty();
                                })
                        )
                        .filter(Objects::nonNull)  // Filter out null results
                        .collectList()
                        .map(accountList -> (Page<AccountResponseDto>) new PageImpl<>(accountList, pageable, accountPage.getTotalElements()))
                )
                .doOnSuccess(result ->
                        log.info("Fetched {} accounts (filtered by alias '{}')", result.getContent().size(), cardAlias)
                )
                .doOnError(error ->
                        log.error("Error retrieving accounts: {}", error.getMessage())
                );
    }

    @Override
    public Mono<Account> getAccountByCustomer(Long id) {
        return Mono.fromCallable(() -> accountRepository.findByCustomerId(id))
                .flatMap(optionalAccount -> optionalAccount
                        .map(account -> {
                            log.info("Fetched account: {}", account);
                            return Mono.just(account);
                        })
                        .orElseGet(() -> {
                            log.warn("Account not found with customerID: {}", id);
                            return Mono.error(new IllegalArgumentException("Account not found"));
                        })
                );
    }
}
