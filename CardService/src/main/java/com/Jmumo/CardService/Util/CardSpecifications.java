package com.Jmumo.CardService.Util;

import com.Jmumo.CardService.domains.Card;
import org.springframework.data.jpa.domain.Specification;

public class CardSpecifications {

    public static Specification<Card> hasAlias(String alias) {
        return (root, query, criteriaBuilder) ->
                alias == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("cardAlias")), "%" + alias.toLowerCase() + "%");
    }

    public static Specification<Card> hasType(String type) {
        return (root, query, criteriaBuilder) ->
                type == null ? null : criteriaBuilder.equal(criteriaBuilder.lower(root.get("cardType")), type.toLowerCase());
    }

    public static Specification<Card> hasPan(String pan) {
        return (root, query, criteriaBuilder) ->
                pan == null ? null : criteriaBuilder.equal(root.get("pan"), pan);
    }
}
