package com.example.myexpensetracker.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public final class Exchange extends Record {
    @NonNull
    private Account account;
    @NonNull
    private Amount fromAmount;
    @NonNull
    private Amount toAmount;

    public Exchange(@NonNull UUID id, @NonNull LocalDate date, @NonNull Account account, @NonNull Amount fromAmount, @NonNull Amount toAmount, String comment) {
        this(id, null, date, account, fromAmount, toAmount, comment);
    }

    public Exchange(@NonNull UUID id, LocalDateTime createdAt, @NonNull LocalDate date, @NonNull Account account, @NonNull Amount fromAmount, @NonNull Amount toAmount, String comment) {
        super(id, createdAt, date, comment);
        this.account = account;
        this.fromAmount = fromAmount;
        this.toAmount = toAmount;
    }

    private Exchange(@NonNull UUID id, @NonNull LocalDate date) {
        super(id, null, date, null);
        fromAmount = new Amount();
        toAmount = new Amount();
    }

    public static Exchange create(@NonNull LocalDate date, @NonNull Account account, @NonNull Amount fromAmount, @NonNull Amount toAmount, String comment) {
        return new Exchange(UUID.randomUUID(), date, account, fromAmount, toAmount, comment);
    }

    public static Exchange create() {
        return new Exchange(UUID.randomUUID(), LocalDate.now());
    }

    public ExchangeRate getRate() {
        return new ExchangeRate(fromAmount.getValue(), toAmount.getValue());
    }

    @Override
    public String toString() {
        return String.format("Exchange[id=%s, date=%s, account=%s, fromAmount=%s, toAmount=%s]",
                getId(), getDate(), account, fromAmount, toAmount);
    }

    @Override
    public Exchange clone() {
        try {
            Exchange clone = (Exchange) super.clone();
            clone.fromAmount = fromAmount.clone();
            clone.toAmount = toAmount.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
