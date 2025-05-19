package com.Jmumo.CardService.web;

import com.Jmumo.CardService.domains.dtos.*;
import com.Jmumo.CardService.services.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardControllerTest {

    private WebTestClient webTestClient;

    @Mock
    private CardService cardService;

    @InjectMocks
    private CardController cardController;


    private CardResponseDto cardResponseDto;
    private CardSearchDto cardSearchDto;




    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToController(cardController).build();

        cardResponseDto = new CardResponseDto();
        cardResponseDto.setPublicId(UUID.randomUUID());
        cardResponseDto.setCardAlias("Test Card");
        cardResponseDto.setCardType("VISA");

        CardSearchDto searchDto = new CardSearchDto();
        searchDto.setCardAlias("Test Card");
        searchDto.setCardType("VISA");
        searchDto.setPan("1234");
        searchDto.setPage(0);     // Set default values
        searchDto.setSize(10);    // Set default values
        searchDto.setMask(false);






    }

    @Test
    void createCard_ShouldReturnCreated() {
        when(cardService.createCard(any()))
                .thenReturn(Mono.just(cardResponseDto));

        webTestClient.post()
                .uri("/cards")
                .bodyValue(new CardCreationDto())
                .exchange()
                .expectStatus().isOk()
                .expectBody(CardResponseDto.class)
                .isEqualTo(cardResponseDto);
    }

    @Test
    void getCardById_ShouldReturnCard() {
        UUID cardId = UUID.randomUUID();
        when(cardService.getCardById(cardId, false))
                .thenReturn(Mono.just(cardResponseDto));

        webTestClient.get()
                .uri("/cards/" + cardId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CardResponseDto.class)
                .isEqualTo(cardResponseDto);
    }


//
//    @Test
//    void searchCards_ShouldReturnFilteredResults() {
//        when(cardService.getAllCards(any(), any(), any(), any(), any(), any()))
//                .thenReturn(Mono.just(List.of(cardResponseDto)));
//
//        webTestClient.get()
//                .uri(uriBuilder -> uriBuilder
//                        .path("/cards/searchCards")
//                        .queryParam("cardAlias", "alias") // Example parameter
//                        .queryParam("accountId", 123)    // Example parameter
//                        .build())
//                .exchange()
//                .expectStatus().isOk()
//                .expectBodyList(CardResponseDto.class)
//                .hasSize(1)
//                .contains(cardResponseDto);
//    }

    @Test
    void updateCard_ShouldReturnUpdatedCard() {
        when(cardService.updateCardAlias(any()))
                .thenReturn(Mono.just(cardResponseDto));

        webTestClient.put()
                .uri("/cards/updateCard")
                .bodyValue(new UpdateCardDto())
                .exchange()
                .expectStatus().isOk()
                .expectBody(CardResponseDto.class)
                .isEqualTo(cardResponseDto);
    }

    @Test
    void deleteCard_ShouldReturnNoContent() {
        webTestClient.delete()
                .uri("/cards/1")
                .exchange()
                .expectStatus().isOk();
    }
}