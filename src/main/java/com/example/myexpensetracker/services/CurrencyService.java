package com.example.myexpensetracker.services;

import com.example.myexpensetracker.domain.model.Currency;
import com.example.myexpensetracker.persistence.entities.CurrencyEntity;
import com.example.myexpensetracker.persistence.mappers.CurrencyMapper;
import com.example.myexpensetracker.persistence.repositories.CurrencyRepository;
import com.example.myexpensetracker.security.AuthService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
public class CurrencyService {
    @Autowired
    private CurrencyRepository repository;

    @Autowired
    private AuthService authService;

    @Autowired
    private CurrencyMapper mapper;

    public List<Currency> getAll() {
        log.debug("Loading currencies");
        List<CurrencyEntity> entities = repository.findByUser_Id(authService.getAuthenticatedUser().getId());
        return mapper.entitiesToDomain(entities);
    }

    public void save(Currency currency) {
        log.debug("Saving: {}", currency);
        CurrencyEntity entity = mapper.domainToEntity(currency);
        entity.setUser(authService.getAuthenticatedUser());
        repository.save(entity);
    }
}
