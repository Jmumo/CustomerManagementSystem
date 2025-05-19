package com.Jmumo.CardService.services.impl;

import com.Jmumo.CardService.Exceptions.CardNotFoundException;
import com.Jmumo.CardService.Exceptions.DuplicateOrExceddingCardException;
import com.Jmumo.CardService.Exceptions.InvalidPanCheckException;
import com.Jmumo.CardService.External.AccountService;
import com.Jmumo.CardService.Util.CardSpecifications;
import com.Jmumo.CardService.datatype.CardType;
import com.Jmumo.CardService.domains.Card;
import com.Jmumo.CardService.domains.dtos.CardCreationDto;
import com.Jmumo.CardService.domains.dtos.CardResponseDto;
import com.Jmumo.CardService.domains.dtos.UpdateCardDto;
import com.Jmumo.CardService.repository.CardRepository;
import com.Jmumo.CardService.services.CardService;
import com.Jmumo.CardService.Util.CardMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@Slf4j
@AllArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final AccountService accountService;
    private final Scheduler jdbcScheduler = Schedulers.boundedElastic();
    private final CardMapper cardMapper;


    @Override
    public Mono<CardResponseDto> createCard(CardCreationDto cardCreationDto) {

        if (!isValidPan(cardCreationDto.getPan())) {
            log.error("Invalid PAN number: {}", cardCreationDto.getPan());
            return Mono.error(new InvalidPanCheckException("Invalid PAN number. Please check your card number."));
        }
        Card card = cardMapper.toEntity(cardCreationDto);
        log.info("Creating new card: {}", card);

        return accountService.getAccountById(cardCreationDto.getAccountPublicId())
                .flatMap(accountResponseDto -> {
                    log.info("Fetched account: {}", accountResponseDto);

                    // Check if a card of the same type already exists for the given accountPublicId
                    if (cardRepository.existsByAccountIdAndCardType(accountResponseDto.getAccountId(),
                            CardType.valueOf(cardCreationDto.getCardType()))) {
                        log.error("Duplicate card type for accountPublicId: {}", cardCreationDto.getAccountPublicId());
                        return Mono.error(new DuplicateOrExceddingCardException(
                                "Cannot add more than one card of the same type."));
                    }

                    // Check if the total number of cards is already 2 for the given accountPublicId
                    if (cardRepository.countByAccountId(accountResponseDto.getAccountId()) >= 2) {
                        log.error("Exceeded card limit for accountPublicId: {}", cardCreationDto.getAccountPublicId());
                        return Mono.error(new DuplicateOrExceddingCardException(
                                "Cannot exceed 2 cards per account."));
                    }

                    // Save card with fetched account ID
                    card.setAccountId(accountResponseDto.getAccountId());
                    return Mono.fromCallable(() -> cardRepository.save(card))
                            .map(savedCard -> {
                                savedCard.setPan(maskSensitiveData(savedCard.getPan()));
                                savedCard.setCvv(maskSensitiveData(savedCard.getCvv()));
                                return cardMapper.toDto(savedCard);
                            });
                })
                .onErrorResume(WebClientResponseException.BadRequest.class, ex -> {
                    log.error("Invalid account ID: {}", cardCreationDto.getAccountPublicId());
                    return Mono.error(new IllegalArgumentException(
                            "Invalid account public id Consider Creating Account first: " + cardCreationDto.getAccountPublicId()));
                })
                .onErrorResume(DataIntegrityViolationException.class, ex -> {
                    log.error("Error saving card: {}", ex.getMessage());
                    return Mono.error(new DuplicateOrExceddingCardException(
                            "Card with the same PAN already exists."));
                });
    }



    @Override
    public Mono<List<CardResponseDto>> getAllCards(boolean mask) {
        return Mono.fromCallable(cardRepository::findAll)
                .map(cards -> cards.stream()
                        .map(card -> {
                            CardResponseDto dto = cardMapper.toDto(card);
                            if (mask) {
                                dto.setPan(maskSensitiveData(dto.getPan()));
                                dto.setCvv(maskSensitiveData(dto.getCvv()));
                            }
                            return dto;
                        })
                        .collect(Collectors.toList()));
    }

    @Override
    public Mono<CardResponseDto> getCardById(UUID id, boolean mask) {
        return Mono.fromCallable(() -> cardRepository.findCardByPublicId(id)
                .map(card -> {
                    CardResponseDto dto = cardMapper.toDto(card);
                    if (mask){
                        dto.setPan(maskSensitiveData(dto.getPan()));
                        dto.setCvv(maskSensitiveData(dto.getCvv()));
                    }
                    return dto;
                }).orElseThrow(() -> new CardNotFoundException("Card not found with ID: " + id))
        ).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<CardResponseDto> updateCardAlias(UpdateCardDto updateCardDto) {
        return Mono.fromCallable(() ->
                cardRepository.findCardByPublicId(updateCardDto.getCardPublicID())
                        .map(card -> {
                            card.setCardAlias(updateCardDto.getCardAlias());
                            return cardRepository.save(card);
                        })
                        .map(card -> {
                            CardResponseDto dto = cardMapper.toDto(card);
                            if (updateCardDto.isMask()) {
                                dto.setPan(maskSensitiveData(dto.getPan()));
                                dto.setCvv(maskSensitiveData(dto.getCvv()));
                            }
                            return dto;
                        })
                        .orElseThrow(() -> new CardNotFoundException("Card not found with ID: " + updateCardDto.getCardPublicID()))
        ).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<CardResponseDto> deleteCard(Long id) {
        return Mono.fromRunnable(() -> cardRepository.deleteById(id));
    }


    @Override
    public Mono<Page<CardResponseDto>> getAllCards(
            String cardAlias, String cardType, String pan, int page, int size, boolean mask) {
        List<Specification<Card>> specs = new ArrayList<>();

        if (cardAlias != null) {
            specs.add(CardSpecifications.hasAlias(cardAlias));
        }
        if (cardType != null) {
            specs.add(CardSpecifications.hasType(cardType));
        }
        if (pan != null) {
            specs.add(CardSpecifications.hasPan(pan));
        }

        Specification<Card> combinedSpec = specs.stream()
                .reduce(Specification::and)
                .orElse((root, query, criteriaBuilder) -> criteriaBuilder.conjunction());

        Pageable pageable = PageRequest.of(page, size);

        return Mono.fromCallable(() -> cardRepository.findAll(combinedSpec, pageable))
                .subscribeOn(Schedulers.boundedElastic())
                .map(pageResults -> {
                    List<CardResponseDto> dtos = pageResults.getContent().stream()
                            .map(card -> {
                                CardResponseDto dto = cardMapper.toDto(card);
                                if (mask) {
                                    dto.setPan(maskSensitiveData(dto.getPan()));
                                    dto.setCvv(maskSensitiveData(dto.getCvv()));
                                }
                                return dto;
                            })
                            .collect(Collectors.toList());
                    return new PageImpl<>(dtos, pageable, pageResults.getTotalElements());
                });
    }

//    public Mono<Page<CardResponseDto>> getAllCards(
//            String cardAlias, String cardType, String pan, int page, int size,boolean mask
//    ) {
//        List<Specification<Card>> specs = new ArrayList<>();
//
//        if (cardAlias != null) {
//            specs.add(CardSpecifications.hasAlias(cardAlias));
//        }
//        if (cardType != null) {
//            specs.add(CardSpecifications.hasType(cardType));
//        }
//        if (pan != null) {
//            specs.add(CardSpecifications.hasPan(pan));
//        }
//
//        Specification<Card> combinedSpec = specs.stream()
//                .reduce(Specification::and)
//                .orElse((root, query, criteriaBuilder) -> criteriaBuilder.conjunction());
//
//        Pageable pageable = PageRequest.of(page, size);
//
//        return Mono.fromCallable(() -> cardRepository.findAll(combinedSpec, pageable).getContent())
//                .subscribeOn(Schedulers.boundedElastic())
//                .map(cards -> cards.stream()
//                        .map(card -> {
//                            CardResponseDto dto = cardMapper.toDto(card);
//                            if (mask) {
//                                dto.setPan(maskSensitiveData(dto.getPan()));
//                                dto.setCvv(maskSensitiveData(dto.getCvv()));
//                            }
//                            return dto;
//                        })
//                        .collect(Collectors.toList()));
//    }



    public static String maskSensitiveData(String data) {
        if (data == null || data.length() < 4) return "****";
        return "*".repeat(data.length());
    }



    public boolean isValidPan(String pan) {
        int sum = 0;
        boolean alternate = false;

        for (int i = pan.length() - 1; i >= 0; i--) {
            int n = Character.getNumericValue(pan.charAt(i));

            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n -= 9;
                }
            }
            sum += n;
            alternate = !alternate;
        }


        return (sum % 10 == 0);
    }


}
