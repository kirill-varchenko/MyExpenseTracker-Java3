package com.example.myexpensetracker.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public final class Expense extends Record {
    @NonNull
    private Account account;
    @NonNull
    private Currency currency;

    private List<Entry> entries = new ArrayList<>();

    public Expense(@NonNull UUID id, LocalDateTime createdAt, @NonNull LocalDate date, String comment) {
        super(id, createdAt, date, comment);
    }

    public static Expense create(@NonNull LocalDate date, String comment) {
        return new Expense(UUID.randomUUID(), null, date, comment);
    }

    public void add(Entry entry) {
        if (!entry.isAmountPositive()) {
            throw new IllegalArgumentException("Amount must be > 0");
        }
        entries.add(entry);
    }

    public Amount getAmount() {
        BigDecimal value = entries.stream().map(Entry::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new Amount(value, currency);
    }

    @Override
    public String toString() {
        return String.format("Expense[id=%s, date=%s, entries=%s]", getId(), getDate(), getEntries());
    }

    @Override
    public Expense clone() {
        try {
            Expense clone = (Expense) super.clone();
            clone.entries = entries.stream().map(Entry::clone).collect(Collectors.toCollection(ArrayList::new));
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Data
    public static class Entry implements Cloneable {
        /*
           This random id is used because otherwise Entry object looks like a Value object
           which is wrong because Entries with same fields are still different entries.
           This affects hash code generation, which is important, because I use Entry objects
           as map keys in ExpenseDialog
         */
        @Getter(AccessLevel.NONE)
        @Setter(AccessLevel.NONE)
        private UUID id = UUID.randomUUID();

        @NonNull
        private BigDecimal amount;
        private Category category;
        private String comment;
        private Set<Tag> tags = new HashSet<>();

        public Entry() {
            this(BigDecimal.ZERO);
        }

        public Entry(@NonNull BigDecimal amount) {
            this.amount = amount;
        }

        public Entry(@NonNull BigDecimal amount, Category category, String comment, Set<Tag> tags) {
            this.amount = amount;
            this.category = category;
            this.comment = comment;
            this.tags = tags;
        }

        public boolean isAmountPositive() {
            return amount.compareTo(BigDecimal.ZERO) > 0;
        }

        @Override
        public Entry clone() {
            try {
                Entry clone = (Entry) super.clone();
                clone.tags = new HashSet<>(tags);
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }
}
