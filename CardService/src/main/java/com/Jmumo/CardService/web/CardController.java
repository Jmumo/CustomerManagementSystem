package com.Jmumo.CardService.web;

import com.Jmumo.CardService.domains.dtos.CardCreationDto;
import com.Jmumo.CardService.domains.dtos.CardResponseDto;
import com.Jmumo.CardService.domains.dtos.CardSearchDto;
import com.Jmumo.CardService.domains.dtos.UpdateCardDto;
import com.Jmumo.CardService.services.CardService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }


    @PostMapping
    public Mono<ResponseEntity<CardResponseDto>> createCard( @RequestBody  CardCreationDto cardCreationDto) {
        return cardService.createCard(cardCreationDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @GetMapping
    public Mono<List<CardResponseDto>> getAllCards(@RequestParam(defaultValue = "false") boolean mask) {
        return cardService.getAllCards(mask);
    }

    @GetMapping("/{id}")
    public Mono<CardResponseDto> getCardById(@PathVariable UUID id, @RequestParam(defaultValue = "false") boolean mask) {
        return cardService.getCardById(id,mask);
    }

    @PutMapping("/updateCard")
    public Mono<CardResponseDto> updateCardAlias(@RequestBody UpdateCardDto updateCardDto ) {
        return cardService.updateCardAlias(updateCardDto);
    }

    @DeleteMapping("/{id}")
    public void deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
    }


    @PostMapping("/searchCards")
    public Mono<Page<CardResponseDto>>getCustomers(
           @RequestBody CardSearchDto cardSearchDto
            ) {
        return cardService.getAllCards(cardSearchDto.getCardAlias(),
                cardSearchDto.getCardType(),
                cardSearchDto.getPan(),
                cardSearchDto.getPage(),
                cardSearchDto.getSize(),
                cardSearchDto.isMask());

    }

}

