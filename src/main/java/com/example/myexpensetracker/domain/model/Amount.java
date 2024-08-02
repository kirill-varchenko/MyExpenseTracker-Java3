package com.example.myexpensetracker.domain.model;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Amount implements Cloneable {
    @NonNull
    private BigDecimal value;
    @NonNull
    private Currency currency;

    public Amount(Currency currency) {
        value = BigDecimal.ZERO;
        this.currency = currency;
    }

    public Amount negate() {
        return new Amount(value.negate(), currency);
    }

    public boolean isPositive() {
        return value.compareTo(BigDecimal.ZERO) > 0;
    }

    public Amount add(Amount other) {
        if (!other.getCurrency().equals(currency)) {
            throw new IllegalArgumentException("Can add only amounts with same currency");
        }
        return new Amount(value.add(other.getValue()), currency);
    }

    @Override
    public String toString() {
        return String.format("%s %s", value, currency.getSymbol());
    }

    @Override
    public Amount clone() {
        try {
            return (Amount) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
