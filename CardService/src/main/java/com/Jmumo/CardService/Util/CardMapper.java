package com.Jmumo.CardService.Util;


import com.Jmumo.CardService.domains.Card;
import com.Jmumo.CardService.domains.dtos.CardCreationDto;
import com.Jmumo.CardService.domains.dtos.CardResponseDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface CardMapper {

    Card toEntity(CardCreationDto dto);
    CardResponseDto toDto(Card card);

    default CardResponseDto mapWithBaseFields(Card card) {
        CardResponseDto dto = toDto(card);
        MappingUtil.mapBaseEntityFieldsToCards(card, dto);
        return dto;
    }
}


