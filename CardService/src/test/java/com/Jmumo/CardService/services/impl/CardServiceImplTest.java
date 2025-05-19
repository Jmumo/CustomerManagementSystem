package com.Jmumo.CardService.services.impl;

import com.Jmumo.CardService.Exceptions.CardNotFoundException;
import com.Jmumo.CardService.Exceptions.DuplicateOrExceddingCardException;
import com.Jmumo.CardService.External.AccountService;
import com.Jmumo.CardService.External.Dtos.AccountResponseDto;
import com.Jmumo.CardService.Util.CardSpecifications;
import com.Jmumo.CardService.datatype.CardType;
import com.Jmumo.CardService.domains.Card;
import com.Jmumo.CardService.domains.dtos.CardCreationDto;
import com.Jmumo.CardService.domains.dtos.CardResponseDto;
import com.Jmumo.CardService.domains.dtos.UpdateCardDto;
import com.Jmumo.CardService.repository.CardRepository;
import com.Jmumo.CardService.Util.CardMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private CardMapper cardMapper;

    @InjectMocks
    private CardServiceImpl cardService;

    private CardCreationDto cardCreationDto;
    private CardResponseDto cardResponseDto;
    private Card card;
    private UpdateCardDto updateCardDto;

    @BeforeEach
    void setUp() {
        // Test data initialization
        cardCreationDto = new CardCreationDto();
        cardCreationDto.setAccountPublicId(UUID.randomUUID());
        cardCreationDto.setCardType(String.valueOf(CardType.PHYSICAL));
        cardCreationDto.setPan("4111111111111111");
        cardCreationDto.setCvv("123");
        cardCreationDto.setCardAlias("My Card");

        cardResponseDto = new CardResponseDto();
        cardResponseDto.setPublicId(UUID.randomUUID());
        cardResponseDto.setCardAlias("My Card");
        cardResponseDto.setCardType(String.valueOf(CardType.PHYSICAL));
        cardResponseDto.setPan("4111111111111111");
        cardResponseDto.setCvv("123");

        card = new Card();
        card.setCardId(1L);

        card.setPublicId(UUID.randomUUID());
        card.setCardAlias("My Card");
        card.setCardType(CardType.PHYSICAL);
        card.setPan("4111111111111111");
        card.setCvv("123");

        updateCardDto = new UpdateCardDto();
        updateCardDto.setCardPublicID(card.getPublicId());
        updateCardDto.setCardAlias("Updated Alias");
        updateCardDto.setMask(false);
    }

    @Test
    void createCard_Success() {
        // Setup account response with account ID
        AccountResponseDto accountResponseDto = new AccountResponseDto();
        accountResponseDto.setPublicId(UUID.randomUUID());
        accountResponseDto.setIban("IBAN123");
        accountResponseDto.setAccountId(1L);  // THIS WAS MISSING

        // Mock account service
        when(accountService.getAccountById(cardCreationDto.getAccountPublicId()))
                .thenReturn(Mono.just(accountResponseDto));

        // Mock repository calls using the same account ID (1L)
        when(cardRepository.existsByAccountIdAndCardType(1L, CardType.PHYSICAL))
                .thenReturn(false);
        when(cardRepository.countByAccountId(1L))
                .thenReturn(0);

        // Mock mapper and save operations
        when(cardMapper.toEntity(cardCreationDto))
                .thenReturn(card);
        when(cardRepository.save(card))
                .thenReturn(card);
        when(cardMapper.toDto(card))
                .thenReturn(cardResponseDto);

        // Execute and verify
        Mono<CardResponseDto> result = cardService.createCard(cardCreationDto);

        StepVerifier.create(result)
                .expectNextMatches(dto ->
                        dto.getPublicId().equals(cardResponseDto.getPublicId()) &&
                                dto.getCardAlias().equals("My Card"))
                .verifyComplete();

        // Verify interactions
        verify(accountService).getAccountById(cardCreationDto.getAccountPublicId());
        verify(cardRepository).existsByAccountIdAndCardType(1L, CardType.PHYSICAL);
        verify(cardRepository).countByAccountId(1L);
        verify(cardRepository).save(card);
    }

    @Test
    void createCard_DuplicateCardType() {

        AccountResponseDto accountResponseDto = new AccountResponseDto();
        accountResponseDto.setPublicId(UUID.randomUUID());
        accountResponseDto.setIban("IBAN123");
        accountResponseDto.setAccountId(1L);
        when(accountService.getAccountById(any(UUID.class)))
                .thenReturn(Mono.just(accountResponseDto));
        when(cardRepository.existsByAccountIdAndCardType(anyLong(), any(CardType.class)))
                .thenReturn(true);

        Mono<CardResponseDto> result = cardService.createCard(cardCreationDto);

        StepVerifier.create(result)
                .expectErrorMatches(ex ->
                        ex instanceof DuplicateOrExceddingCardException &&
                                ex.getMessage().equals("Cannot add more than one card of the same type."))
                .verify();
    }

    @Test
    void createCard_ExceedCardLimit() {

        AccountResponseDto accountResponseDto = new AccountResponseDto();
        accountResponseDto.setPublicId(UUID.randomUUID());
        accountResponseDto.setIban("IBAN123");
        accountResponseDto.setAccountId(1L);
        when(accountService.getAccountById(any(UUID.class)))
                .thenReturn(Mono.just(accountResponseDto));
        when(cardRepository.existsByAccountIdAndCardType(anyLong(), any(CardType.class)))
                .thenReturn(false);
        when(cardRepository.countByAccountId(anyLong()))
                .thenReturn(2);

        Mono<CardResponseDto> result = cardService.createCard(cardCreationDto);

        StepVerifier.create(result)
                .expectErrorMatches(ex ->
                        ex instanceof DuplicateOrExceddingCardException &&
                                ex.getMessage().equals("Cannot exceed 2 cards per account."))
                .verify();
    }

    @Test
    void createCard_InvalidAccountId() {
        when(accountService.getAccountById(any(UUID.class)))
                .thenReturn(Mono.error(WebClientResponseException.BadRequest.create(400, "Bad Request", null, null, null)));

        Mono<CardResponseDto> result = cardService.createCard(cardCreationDto);

        StepVerifier.create(result)
                .expectErrorMatches(ex ->
                        ex instanceof IllegalArgumentException &&
                                ex.getMessage().contains("Invalid account public id"))
                .verify();
    }


    @Test
    void getCardById_NotFound() {
        UUID cardId = UUID.randomUUID();
        when(cardRepository.findCardByPublicId(cardId))
                .thenReturn(Optional.empty());

        Mono<CardResponseDto> result = cardService.getCardById(cardId, false);

        StepVerifier.create(result)
                .expectErrorMatches(ex ->
                        ex instanceof CardNotFoundException &&
                                ex.getMessage().equals("Card not found with ID: " + cardId))
                .verify();
    }

    @Test
    void updateCardAlias_Success() {
        when(cardRepository.findCardByPublicId(any(UUID.class)))
                .thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class)))
                .thenReturn(card);
        when(cardMapper.toDto(any(Card.class)))
                .thenReturn(cardResponseDto);

        Mono<CardResponseDto> result = cardService.updateCardAlias(updateCardDto);

        StepVerifier.create(result)
                .expectNextMatches(dto ->
                        dto.getCardAlias().equals("My Card")) // Original alias since we didn't mock the update
                .verifyComplete();

        verify(cardRepository).save(card);
    }

    @Test
    void getAllCardsWithFilters_Success() {
        // Given
        Specification<Card> spec = CardSpecifications.hasAlias("My Card");
        Pageable pageable = PageRequest.of(0, 10);
        Page<Card> cardPage = new PageImpl<>(List.of(card), pageable, 1);
        Page<CardResponseDto> expectedResponsePage = new PageImpl<>(
                List.of(cardResponseDto),
                pageable,
                1
        );

        // When
        when(cardRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(cardPage);
        when(cardMapper.toDto(card))
                .thenReturn(cardResponseDto);

        Mono<Page<CardResponseDto>> result = cardService.getAllCards(
                "My Card", null, null, 0, 10, false);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(actualPage -> {
                    // Verify page content
                    boolean contentMatches = actualPage.getContent().size() == 1 &&
                            actualPage.getContent().get(0).getCardAlias().equals("My Card");

                    // Verify pagination metadata
                    boolean pageableMatches = actualPage.getPageable().equals(pageable);
                    boolean totalMatches = actualPage.getTotalElements() == 1;

                    return contentMatches && pageableMatches && totalMatches;
                })
                .verifyComplete();

        // Verify interactions
        verify(cardRepository).findAll(any(Specification.class), eq(pageable));
        verify(cardMapper).toDto(card);
    }

    @Test
    void deleteCard_Success() {
        Long cardId = 1L;
        doNothing().when(cardRepository).deleteById(cardId);

        Mono<CardResponseDto> result = cardService.deleteCard(cardId);

        StepVerifier.create(result)
                .verifyComplete();

        verify(cardRepository).deleteById(cardId);
    }

    @Test
    void isValidPan_ValidNumber() {
        assertTrue(cardService.isValidPan("4111111111111111"));
    }

    @Test
    void isValidPan_InvalidNumber() {
        assertFalse(cardService.isValidPan("4111111111111112"));
    }

    @Test
    void maskSensitiveData_FullMask() {
        assertEquals("****", CardServiceImpl.maskSensitiveData("123"));
        assertEquals("*****", CardServiceImpl.maskSensitiveData("41111"));
    }
}