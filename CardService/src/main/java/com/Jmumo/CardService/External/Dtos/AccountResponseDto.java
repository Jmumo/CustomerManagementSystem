package com.Jmumo.CardService.External.Dtos;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AccountResponseDto {
    private UUID publicId;
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;
    private int version;
    private Long accountId;
    private String iban;
    private String bicSwift;
    private Long customerId;
}
