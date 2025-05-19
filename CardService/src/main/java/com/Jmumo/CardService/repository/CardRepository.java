package com.Jmumo.CardService.repository;

import com.Jmumo.CardService.datatype.CardType;
import com.Jmumo.CardService.domains.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

public interface CardRepository extends JpaSpecificationExecutor<Card> ,JpaRepository<Card, Long> {

    Optional<Card> findCardByPublicId(UUID publicId);


//    int countByPublicId(UUID accountId);
//
//    int countByPublicIdAndCardType(UUID accountId, CardType cardType);

    boolean existsByAccountIdAndCardType(Long accountId, CardType cardType);



    int countByAccountId(Long accountId);
}
