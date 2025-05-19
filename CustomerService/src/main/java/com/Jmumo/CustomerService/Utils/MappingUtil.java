package com.Jmumo.CustomerService.Utils;

import com.Jmumo.CustomerService.domain.BaseEntity;
import com.Jmumo.CustomerService.domain.Dtos.CustomerResponseDto;

public class MappingUtil {
    public static void mapBaseEntityFieldsToCards(BaseEntity source, CustomerResponseDto target) {
        target.setPublicId(source.getPublicId());
        target.setCreatedDate(source.getCreatedDate());
        target.setUpdateDate(source.getUpdateDate());
    }
}
