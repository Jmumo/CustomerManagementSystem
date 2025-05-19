package com.Jmumo.AccountService.domains.Dtos;


import lombok.Data;

@Data
public class AccountSearchRequest {
    private String iban;
    private String bicSwift;
    private String cardAlias;
    private int page = 0;
    private int size = 10;
}
