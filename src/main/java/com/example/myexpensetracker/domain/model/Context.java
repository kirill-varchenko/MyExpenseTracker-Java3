package com.example.myexpensetracker.domain.model;

import java.util.Comparator;
import java.util.List;

public record Context(List<Account> accounts, List<Currency> currencies, List<Category> categories, List<Tag> tags,
                      Profile profile) {

    public List<Account> getActiveAccounts() {
        return accounts.stream().filter(Account::isActive).toList();
    }

    public List<Currency> getActiveCurrencies() {
        return currencies.stream().filter(Currency::isActive).sorted(Comparator.comparingInt(Currency::getOrder)).toList();
    }
}
