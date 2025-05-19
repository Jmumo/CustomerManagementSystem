package com.Jmumo.CardService.domains.dtos;


import lombok.Data;

@Data
public class CardSearchDto {
    private String cardAlias;
    private String cardType;
    private String pan;
    private int page = 0;
    private int size = 10;
    private boolean mask;
}
