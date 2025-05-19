package com.Jmumo.CustomerService.domain.Dtos;


import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class CustomerCreationDto {
    @NotNull(message = "firstName cannot be null")
    private String firstName;
    @NotNull(message = "firstName cannot be null")
    private String secondName;
    private String otherNames;
}
