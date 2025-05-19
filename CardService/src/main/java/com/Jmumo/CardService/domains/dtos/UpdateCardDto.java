package com.Jmumo.CardService.domains.dtos;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateCardDto {
    @NotNull(message = "cardAlias cannot be null")
    private String cardAlias;
    private UUID cardPublicID;
    private boolean mask;
}
