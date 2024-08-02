package com.example.myexpensetracker.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public final class Income extends Record implements Cloneable {
    @NonNull
    private Account account;
    @NonNull
    private Amount amount = new Amount();
    private Category category;
    private Set<Tag> tags = new HashSet<>();

    public Income(@NonNull UUID id, LocalDateTime createdAt, @NonNull LocalDate date, String comment) {
        super(id, createdAt, date, comment);
    }

    public static Income create(LocalDate date, String comment) {
        return new Income(UUID.randomUUID(), null, date, comment);
    }

    @Override
    public String toString() {
        return String.format("Income[id=%s, date=%s, account=%s, amount=%s, category=%s, tags=%s]", getId(), getDate(), account, amount, category, tags);
    }

    @Override
    public Income clone() {
        try {
            Income clone = (Income) super.clone();
            clone.amount = amount.clone();
            clone.tags =new HashSet<>(tags);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
