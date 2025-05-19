package com.Jmumo.AccountService.domains.Dtos;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class AccountResponseDto {
    private UUID publicId;
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;
    private Long accountId;
    private int version;
    private String iban;
    private String bicSwift;
    private Long customerId;
    List<CardDto> cards;
}
