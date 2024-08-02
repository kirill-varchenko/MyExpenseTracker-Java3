package com.example.myexpensetracker.persistence.mappers;

import com.example.myexpensetracker.domain.model.Account;
import com.example.myexpensetracker.persistence.entities.AccountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountMapper {
    @Mapping(target = "user", ignore = true)
    AccountEntity domainToEntity(Account account);

    @Mapping(target = "children", ignore = true)
    Account entityToDomain(AccountEntity entity);

    List<Account> entitiesToDomain(List<AccountEntity> entities);
}
