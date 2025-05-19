package com.Jmumo.AccountService.web;

import com.Jmumo.AccountService.domains.Account;
import com.Jmumo.AccountService.domains.Dtos.AccountCreationDto;
import com.Jmumo.AccountService.domains.Dtos.AccountResponseDto;
import com.Jmumo.AccountService.domains.Dtos.AccountSearchRequest;
import com.Jmumo.AccountService.domains.Dtos.CardDto;
import com.Jmumo.AccountService.services.AccountService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Nested
@WebFluxTest(AccountController.class)
@ExtendWith(SpringExtension.class)
class AccountControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private AccountService accountService;


    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createAccount() {
        AccountCreationDto accountCreationDto = new AccountCreationDto();
        accountCreationDto.setIban("DE89370400440532013000");
        accountCreationDto.setBicSwift("COBADEFFXXX");
        accountCreationDto.setCustomerId(UUID.randomUUID());
        AccountResponseDto responseDto = new AccountResponseDto();
        responseDto.setIban("DE89370400440532013000");
        responseDto.setBicSwift("COBADEFFXXX");
        responseDto.setCards(new ArrayList<>());
        responseDto.setAccountId(2L);
        responseDto.setCustomerId(1L);
        responseDto.setCreatedDate(LocalDateTime.now());

        when(accountService.createAccount(accountCreationDto))
                .thenReturn(Mono.just(responseDto));

        webTestClient.post().uri("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(accountCreationDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AccountResponseDto.class)
                .value(response -> assertEquals("COBADEFFXXX", response.getBicSwift()));
    }




    @Test
    void getAllAccounts() {

       Account account = new Account();
       account.setIban("DE89370400440532013000");
       account.setBicSwift("COBADEFFXXX");
       account.setCustomerId(1L);
       account.setCreatedDate(LocalDateTime.now());


        List<Account> accounts = List.of(account);

        when(accountService.getAllAccounts())
                .thenReturn(Mono.just(accounts));

        webTestClient.get().uri("/accounts")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Account.class)
                .hasSize(1);
    }

    @Test
    void getAccountById() {
        UUID id = UUID.randomUUID();
        Account account = new Account();
        account.setAccountId(1L);
        account.setIban("DE89370400440532013000");
        account.setBicSwift("COBADEFFXXX");
        account.setCustomerId(1L);
        account.setCreatedDate(LocalDateTime.now());

        when(accountService.getAccountById(id))
                .thenReturn(Mono.just(account));

        webTestClient.get().uri("/accounts/{id}", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Account.class)
                .value(a -> assertEquals(1L, a.getAccountId()));
    }

    @Test
    void getAccountByCustomerId() {

        Long id = 1L;
        Account account = new Account();
        account.setAccountId(1L);
        account.setIban("DE89370400440532013000");
        account.setBicSwift("COBADEFFXXX");
        account.setCustomerId(3L);
        account.setCreatedDate(LocalDateTime.now());

        when(accountService.getAccountByCustomer(id))
                .thenReturn(Mono.just(account));

        webTestClient.get().uri("/accounts/ByCustomerId/{id}", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Account.class)
                .value(a -> assertEquals(3L, a.getCustomerId()));
    }

    @Test
    void updateAccount() {

        Long id = 1L;
        Account updatedAccount = new Account();
        updatedAccount.setIban("DE89370400440532013000");
        updatedAccount.setBicSwift("COBADEFFXXX");
        updatedAccount.setCustomerId(1L);

        when(accountService.updateAccount(id, updatedAccount))
                .thenReturn(Mono.just(updatedAccount));

        webTestClient.put().uri("/accounts/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedAccount)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Account.class)
                .value(a -> assertEquals("COBADEFFXXX", updatedAccount.getBicSwift()));

    }

    @Test
    void deleteAccount() {
        Long id = 1L;
        when(accountService.deleteAccount(id)).thenReturn(Mono.empty());
        webTestClient.delete().uri("/accounts/{id}", id)
                .exchange()
                .expectStatus().isOk();


    }



    @Test
    void testGetAllAccounts() {
        // Setup test data
        AccountSearchRequest searchRequest = new AccountSearchRequest();
        searchRequest.setIban("DE89370400440532013000");
        searchRequest.setBicSwift("COBADEFFXXX");
        searchRequest.setCardAlias("elias");
        searchRequest.setPage(0);
        searchRequest.setSize(10);

        // Create test response DTOs
        AccountResponseDto responseDto = new AccountResponseDto();
        responseDto.setIban("DE89370400440532013000");
        responseDto.setBicSwift("COBADEFFXXX");
        responseDto.setCards(List.of(new CardDto()));

        AccountResponseDto responseDto1 = new AccountResponseDto();
        responseDto1.setIban("DE89370400440532013001");
        responseDto1.setBicSwift("COBADEFFXXX");
        responseDto1.setCards(List.of(new CardDto()));

        // Create a page of results
        Page<AccountResponseDto> accountPage = new PageImpl<>(
                List.of(responseDto, responseDto1),
                PageRequest.of(searchRequest.getPage(), searchRequest.getSize()),
                2L // total elements
        );

        // Mock the service call
        when(accountService.getAllAccounts(
                searchRequest.getIban(),
                searchRequest.getBicSwift(),
                searchRequest.getPage(),
                searchRequest.getSize(),
                searchRequest.getCardAlias()
        )).thenReturn(Mono.just(accountPage));

        // Test the endpoint
        webTestClient.post().uri("/accounts/search")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(searchRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content.length()").isEqualTo(2) // Verify page content size
                .jsonPath("$.content[0].iban").isEqualTo("DE89370400440532013000")
                .jsonPath("$.content[1].iban").isEqualTo("DE89370400440532013001")
                .jsonPath("$.totalElements").isEqualTo(2); // Verify total elements
    }
}