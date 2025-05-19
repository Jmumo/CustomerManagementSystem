package com.Jmumo.CustomerService.Utils;


import com.Jmumo.CustomerService.domain.Customer;
import com.Jmumo.CustomerService.domain.Dtos.CustomerCreationDto;
import com.Jmumo.CustomerService.domain.Dtos.CustomerResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    Customer toEntity(CustomerCreationDto dto);
    CustomerResponseDto toDto(Customer card);

    default CustomerResponseDto mapWithBaseFields(Customer customer) {
        CustomerResponseDto dto = toDto(customer);
        MappingUtil.mapBaseEntityFieldsToCards(customer, dto);
        return dto;
    }
}
