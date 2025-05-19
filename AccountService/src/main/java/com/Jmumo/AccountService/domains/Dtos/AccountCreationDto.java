package com.Jmumo.AccountService.domains.Dtos;


import lombok.Data;

import java.util.UUID;

@Data
public class AccountCreationDto {
    private String iban;
    private String bicSwift;
    private UUID customerId;
}
