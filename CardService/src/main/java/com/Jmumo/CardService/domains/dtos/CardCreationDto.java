package com.Jmumo.CardService.domains.dtos;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.UUID;


@Data
public class CardCreationDto {
    private String cardAlias;
    private UUID accountPublicId;
    private String cardType;
    private String pan;
    @NotNull(message = "CVV cannot be null")
    @Pattern(regexp = "\\d{3}", message = "CVV must be exactly 3 digits")
    private String cvv;
    boolean mask;
}
