package com.Jmumo.CustomerService.External.Dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
public class CardDto {
    private UUID publicId;
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;
    private int version;
    private String cardAlias;
    private String cardType;
    private String pan;
    private String cvv;
}
