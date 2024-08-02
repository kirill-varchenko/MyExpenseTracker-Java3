package com.example.myexpensetracker.persistence.mappers;

import com.example.myexpensetracker.domain.model.Currency;
import com.example.myexpensetracker.persistence.entities.CurrencyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CurrencyMapper {
    @Mapping(target = "user", ignore = true)
    CurrencyEntity domainToEntity(Currency currency);

    Currency entityToDomain(CurrencyEntity entity);

    List<Currency> entitiesToDomain(List<CurrencyEntity> entities);
}
