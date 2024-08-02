package com.example.myexpensetracker.services;

import com.example.myexpensetracker.domain.model.Account;
import com.example.myexpensetracker.persistence.entities.AccountEntity;
import com.example.myexpensetracker.persistence.mappers.AccountMapper;
import com.example.myexpensetracker.persistence.repositories.AccountRepository;
import com.example.myexpensetracker.security.AuthService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@Service
public class AccountService {
    @Autowired
    private AccountRepository repository;

    @Autowired
    private AuthService authService;

    @Autowired
    private AccountMapper mapper;

    public List<Account> getAll() {
        log.debug("Loading accounts");
        List<AccountEntity> accountEntities = repository.findByUser_Id(authService.getAuthenticatedUser().getId());
        List<Account> accounts = mapper.entitiesToDomain(accountEntities);
        packChildren(accounts);
        return accounts;
    }

    public void save(Account account) {
        log.debug("Saving: {}", account);
        AccountEntity entity = mapper.domainToEntity(account);
        entity.setUser(authService.getAuthenticatedUser());
        repository.save(entity);
    }

    private void packChildren(List<Account> accounts) {
        Map<UUID, Account> accountMap = accounts.stream().collect(Collectors.toMap(Account::getId, Function.identity()));
        for (Account account : accounts) {
            if (account.getParent() != null) {
                accountMap.get(account.getParent().getId()).getChildren().add(account);
            }
        }
    }
}
