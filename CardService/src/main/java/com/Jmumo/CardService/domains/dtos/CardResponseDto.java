package com.Jmumo.CardService.domains.dtos;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CardResponseDto {
    private UUID publicId;
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;
    private int version;
    private String cardAlias;
    private String cardType;
    private String pan;
    private String cvv;
}
