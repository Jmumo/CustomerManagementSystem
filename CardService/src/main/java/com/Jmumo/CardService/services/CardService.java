package com.Jmumo.CardService.services;

import com.Jmumo.CardService.domains.Card;

import com.Jmumo.CardService.domains.dtos.CardCreationDto;
import com.Jmumo.CardService.domains.dtos.CardResponseDto;
import com.Jmumo.CardService.domains.dtos.UpdateCardDto;
import org.springframework.data.domain.Page;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CardService {

    Mono<CardResponseDto> createCard(CardCreationDto card);

    Mono<List<CardResponseDto>> getAllCards(boolean mask);

    Mono<CardResponseDto> getCardById(UUID id, boolean mask);

    Mono<CardResponseDto> updateCardAlias(UpdateCardDto updateCardDto );

    Mono<CardResponseDto> deleteCard(Long id);

    Mono<Page<CardResponseDto>> getAllCards(
            String cardAlias, String cardType, String pan, int page, int size, boolean mask
    );
}
