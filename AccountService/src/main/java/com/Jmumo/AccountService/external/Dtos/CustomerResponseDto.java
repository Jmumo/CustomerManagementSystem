package com.Jmumo.AccountService.external.Dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;
@Data
public class CustomerResponseDto {

    private UUID publicId;
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;
    private int version;
    private Long customerId;
    private String firstName;
    private String secondName;
    private String otherNames;
}
