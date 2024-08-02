package com.example.myexpensetracker.services;

import com.example.myexpensetracker.domain.model.Profile;
import com.example.myexpensetracker.persistence.entities.ProfileEntity;
import com.example.myexpensetracker.persistence.mappers.AccountMapper;
import com.example.myexpensetracker.persistence.mappers.CurrencyMapper;
import com.example.myexpensetracker.persistence.repositories.ProfileRepository;
import com.example.myexpensetracker.security.AuthService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class ProfileService {
    @Autowired
    private AuthService authService;

    @Autowired
    private CurrencyMapper currencyMapper;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private ProfileRepository repository;

    public Profile get() {
        ProfileEntity entity = authService.getAuthenticatedUser().getProfile();
        return new Profile(currencyMapper.entityToDomain(entity.getBaseCurrency()),
                currencyMapper.entityToDomain(entity.getDefaultCurrency()),
                accountMapper.entityToDomain(entity.getDefaultAccount()));
    }

    public void save(Profile profile) {
        log.debug("Saving profile");
        ProfileEntity entity = authService.getAuthenticatedUser().getProfile();
        entity.setBaseCurrency(currencyMapper.domainToEntity(profile.getBaseCurrency()));
        entity.setDefaultCurrency(currencyMapper.domainToEntity(profile.getDefaultCurrency()));
        entity.setDefaultAccount(accountMapper.domainToEntity(profile.getDefaultAccount()));
        repository.save(entity);
    }
}
