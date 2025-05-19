package com.Jmumo.AccountService.web;

import com.Jmumo.AccountService.domains.Account;
import com.Jmumo.AccountService.domains.Dtos.AccountCreationDto;
import com.Jmumo.AccountService.domains.Dtos.AccountResponseDto;
import com.Jmumo.AccountService.domains.Dtos.AccountSearchRequest;
import com.Jmumo.AccountService.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public Mono<AccountResponseDto> createAccount(@RequestBody AccountCreationDto accountCreationDto) {
        return accountService.createAccount(accountCreationDto);
    }

    @GetMapping
    public Mono<List<Account>> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/{id}")
    public Mono<Account> getAccountById(@PathVariable UUID id) {
        return accountService.getAccountById(id);
    }

    @GetMapping("/ByCustomerId/{id}")
    public Mono<Account> getAccountByCustomerId(@PathVariable Long id) {
        return accountService.getAccountByCustomer(id);
    }

    @PutMapping("/{id}")
    public Mono<Account> updateAccount(@PathVariable Long id, @RequestBody Account updatedAccount) {
        return accountService.updateAccount(id, updatedAccount);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteAccount(@PathVariable Long id) {
        return accountService.deleteAccount(id);
    }

    @PostMapping("/search")
    public Mono<Page<AccountResponseDto>> getAllAccounts(
            @RequestBody AccountSearchRequest searchRequest) {

        return accountService.getAllAccounts(
                searchRequest.getIban(),
                searchRequest.getBicSwift(),
                searchRequest.getPage(),
                searchRequest.getSize(),
                searchRequest.getCardAlias()
        );
    }



}
