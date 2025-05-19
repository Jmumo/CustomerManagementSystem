package com.Jmumo.AccountService.utils;

import com.Jmumo.AccountService.domains.BaseEntity;
import com.Jmumo.AccountService.domains.Dtos.AccountResponseDto;

public class MappingUtil {
    public static void mapBaseEntityFieldsToCards(BaseEntity source, AccountResponseDto target) {
        target.setPublicId(source.getPublicId());
        target.setCreatedDate(source.getCreatedDate());
        target.setUpdateDate(source.getUpdateDate());
    }
}
