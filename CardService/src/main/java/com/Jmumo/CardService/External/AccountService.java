package com.Jmumo.CardService.External;

import com.Jmumo.CardService.External.Dtos.AccountResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import javax.security.auth.login.AccountNotFoundException;
import java.util.UUID;

@Slf4j
@Service
public class AccountService {
    private final WebClient webClient;

    public AccountService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<AccountResponseDto> getAccountById(UUID accountId) {
        log.info("Get account by id: {}", accountId);
        String url = String.format("http://localhost:7072/accounts/%s", accountId);

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(AccountResponseDto.class)
                .doOnSuccess(account -> log.info("Successfully fetched account: {}", account))
                .doOnError(error -> log.error("Failed to fetch account details: {}", error.getMessage()))
                .onErrorMap(WebClientResponseException.NotFound.class, ex ->
                        new AccountNotFoundException("Account not found for ID: " + accountId)
                );
    }
}
