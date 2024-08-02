package com.example.myexpensetracker.services;

import com.example.myexpensetracker.domain.model.Context;
import com.example.myexpensetracker.persistence.entities.UserEntity;
import com.example.myexpensetracker.security.AuthService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Delivers context, operates as a context cache.
 */
@Log4j2
@Service
public class ContextService {
    @Autowired
    private AccountService accountService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TagService tagService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private AuthService authService;

    private Context context = null;
    private UUID currentUserId = null;

    public Context get() {
        if (!authService.getAuthenticatedUser().getId().equals(currentUserId)) {
            reset();
            currentUserId = authService.getAuthenticatedUser().getId();
        }
        if (context == null) {
            reload();
        }
        return context;
    }

    public void reload() {
        log.info("Loading context");
        context = new Context(accountService.getAll(), currencyService.getAll(), categoryService.getAll(), tagService.getAll(), profileService.get());
    }

    public void reset() {
        log.info("Reset context");
        context = null;
        currentUserId = null;
    }
}
