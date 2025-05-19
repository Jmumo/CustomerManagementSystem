package com.Jmumo.CustomerService.External;

import com.Jmumo.CustomerService.External.Dtos.AccountResponseDto;
import com.Jmumo.CustomerService.External.Dtos.CardDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final WebClient.Builder webClientBuilder;

    public ExternalClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }


    private WebClient createClient(String baseUrl) {
        return webClientBuilder.baseUrl(baseUrl).build();
    }

    public Mono<List<CardDto>> fetchCardsForAccount(Long id) {
        WebClient webClient = createClient("http://localhost:7071");

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/cards")
                        .queryParam("accountId",id)
                        .build())
                .retrieve()
                .bodyToFlux(CardDto.class)
                .collectList()
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error("Error fetching cards for account ID {}: {}", id, ex.getMessage());
                    return Mono.just(Collections.emptyList());
                });
    }

    public Mono<AccountResponseDto> GetCustomerAccount(Long id) {
        WebClient webClient = createClient("http://localhost:7072");

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/accounts/ByCustomerId/{id}")
                        .build(id))
                .retrieve()
                .bodyToMono(AccountResponseDto.class)  // Use bodyToMono for a single object
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error("Error fetching customer with public ID {}: {}", id, ex.getMessage());
                    return Mono.empty();  // Return an empty Mono in case of an error
                });
    }
}
