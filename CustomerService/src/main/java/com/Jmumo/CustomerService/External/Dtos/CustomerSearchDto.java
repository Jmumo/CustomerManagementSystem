package com.Jmumo.CustomerService.External.Dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Data
public class CustomerSearchDto {
    private UUID publicId;
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;
    private int version;
    private String firstName;
    private String secondName;
    private String otherNames;
    private List<AccountResponseDto> accounts = new ArrayList<>();
}
