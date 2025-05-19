package com.Jmumo.AccountService.utils;


import com.Jmumo.AccountService.domains.Account;
import com.Jmumo.AccountService.domains.Dtos.AccountCreationDto;
import com.Jmumo.AccountService.domains.Dtos.AccountResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    @Mapping(target = "customerId", ignore = true)
    Account toEntity(AccountCreationDto accountCreationDto);
    AccountResponseDto toDto(Account account);

    default AccountResponseDto mapWithBaseFields(Account account) {
        AccountResponseDto dto = toDto(account);
        MappingUtil.mapBaseEntityFieldsToCards(account, dto);
        return dto;
    }
}
