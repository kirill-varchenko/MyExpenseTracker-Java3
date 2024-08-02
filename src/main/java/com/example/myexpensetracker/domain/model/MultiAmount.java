package com.example.myexpensetracker.domain.model;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a "bag" of Amounts with different currencies.
 */
public class MultiAmount {
    private final Map<Currency, BigDecimal> amounts = new HashMap<>();

    public MultiAmount add(Amount amount) {
        Currency currency = amount.getCurrency();
        if (!amounts.containsKey(currency)) {
            amounts.put(currency, amount.getValue());
        } else {
            BigDecimal newAmount = amounts.get(currency).add(amount.getValue());
            amounts.put(currency, newAmount);
        }
        return this;
    }

    public BigDecimal getAmount(Currency currency) {
        return amounts.getOrDefault(currency, BigDecimal.ZERO);
    }

    public Stream<Amount> getAmountStream() {
        return amounts.entrySet().stream().map(entry -> new Amount(entry.getValue(), entry.getKey()));
    }

    @Override
    public String toString() {
        return getAmountStream()
                .sorted(Comparator.comparing(Amount::getCurrency))
                .map(Amount::toString)
                .collect(Collectors.joining(", "));
    }
}
