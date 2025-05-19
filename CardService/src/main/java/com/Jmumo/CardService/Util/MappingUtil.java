package com.Jmumo.CardService.Util;

import com.Jmumo.CardService.domains.BaseEntity;
import com.Jmumo.CardService.domains.dtos.CardResponseDto;

public class MappingUtil {
    public static void mapBaseEntityFieldsToCards(BaseEntity source, CardResponseDto target) {
        target.setPublicId(source.getPublicId());
        target.setCreatedDate(source.getCreatedDate());
        target.setUpdateDate(source.getUpdateDate());
    }
}