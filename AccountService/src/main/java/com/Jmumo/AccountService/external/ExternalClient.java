package com.Jmumo.AccountService.external;

import com.Jmumo.AccountService.configs.AppProperties;
import com.Jmumo.AccountService.domains.Account;
import com.Jmumo.AccountService.domains.Dtos.CardDto;
import com.Jmumo.AccountService.external.Dtos.CustomerResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.UUID;


@Slf4j
@Service
public class ExternalClient {

    private final AppProperties appProperties;

    private final WebClient.Builder webClientBuilder;

    @Autowired
    public ExternalClient(AppProperties appProperties, WebClient.Builder webClientBuilder) {
        this.appProperties = appProperties;
        this.webClientBuilder = webClientBuilder;
    }

    private WebClient createClient(String baseUrl) {
        return webClientBuilder.baseUrl(baseUrl).build();
    }

    public Mono<List<CardDto>> fetchCardsForAccount(Account account) {
        WebClient webClient = createClient(appProperties.getBaseUrl());

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/cards")
                        .queryParam("accountId", account.getAccountId())
                        .build())
                .retrieve()
                .bodyToFlux(CardDto.class)
                .collectList()
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error("Error fetching cards for account ID {}: {}", account.getAccountId(), ex.getMessage());
                    return Mono.just(Collections.emptyList());
                });
    }

    public Mono<CustomerResponseDto> GetCustomerAccount(UUID id) {
        WebClient webClient = createClient(appProperties.getBaseUrl());

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/customers/ByPublicId")
                        .queryParam("id", id)
                        .build(id))
                .retrieve()
                .bodyToMono(CustomerResponseDto.class)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error("Error fetching customer with public ID {}: {}", id, ex.getMessage());
                    return Mono.empty();
                });
    }


}
