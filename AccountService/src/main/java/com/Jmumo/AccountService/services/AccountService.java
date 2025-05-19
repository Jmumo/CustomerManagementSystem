package com.Jmumo.AccountService.services;

import com.Jmumo.AccountService.domains.Account;
import com.Jmumo.AccountService.domains.Dtos.AccountCreationDto;
import com.Jmumo.AccountService.domains.Dtos.AccountResponseDto;
import org.springframework.data.domain.Page;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface AccountService {

    Mono<AccountResponseDto> createAccount(AccountCreationDto accountCreationDto);

    Mono<List<Account>> getAllAccounts();

    Mono<Account> getAccountById(UUID id);

    Mono<Account> updateAccount(Long id, Account updatedAccount);

    Mono<Void> deleteAccount(Long id);

    Mono<Page<AccountResponseDto>> getAllAccounts(
            String iban, String bicSwift, int page, int size,String cardElias);

    Mono<Account> getAccountByCustomer(Long id);
}
