package com.example.myexpensetracker.domain.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public final class Transfer extends Record {
    @NonNull
    private Account fromAccount;
    @NonNull
    private Account toAccount;
    @NonNull
    private Amount amount;

    public Transfer(@NonNull UUID id, @NonNull LocalDate date, @NonNull Account fromAccount, @NonNull Account toAccount, @NonNull Amount amount, String comment) {
        this(id, null, date, fromAccount, toAccount, amount, comment);
    }

    public Transfer(@NonNull UUID id, LocalDateTime createdAt, @NonNull LocalDate date, @NonNull Account fromAccount, @NonNull Account toAccount, @NonNull Amount amount, String comment) {
        super(id, createdAt, date, comment);
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
    }

    private Transfer(@NonNull UUID id, @NonNull LocalDate date) {
        super(id, null, date, null);
        amount = new Amount();
    }

    public static Transfer create(@NonNull LocalDate date, @NonNull Account fromAccount, @NonNull Account toAccount, @NonNull Amount amount, String comment) {
        return new Transfer(UUID.randomUUID(), date, fromAccount, toAccount, amount, comment);
    }

    public static Transfer create() {
        return new Transfer(UUID.randomUUID(), LocalDate.now());
    }

    @Override
    public String toString() {
        return String.format("Transfer[id=%s, date=%s, fromAccount=%s, toAccount=%s, amount=%s]",
                getId(), getDate(), fromAccount, toAccount, amount);
    }

    @Override
    public Transfer clone() {
        try {
            Transfer clone = (Transfer) super.clone();
            clone.amount = amount.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
