package com.Jmumo.CardService.domains;

import com.Jmumo.CardService.datatype.CardType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;



@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "CARDS")
@Data
@ToString(callSuper = true)
public class Card  extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CARD_ID",unique = true, nullable = false,updatable = false)
    private Long cardId;

    @Column(name = "CARD_ALIAS")
    private String cardAlias;

    @Column(name = "ACCOUNT_ID", nullable = false, updatable = false)
    private Long accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "CARD_TYPE", nullable = false , updatable = false)
    private CardType cardType;

    @Column(name = "PAN", nullable = false, unique = true)
    private String pan;

    @Column(name = "CVV", nullable = false)
    @Pattern(regexp = "\\d{3}", message = "CVV must be exactly 3 digits")
    private String cvv;
}
