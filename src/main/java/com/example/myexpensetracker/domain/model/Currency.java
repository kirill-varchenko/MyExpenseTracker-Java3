package com.example.myexpensetracker.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class Currency implements Cloneable, Comparable<Currency> {
    @NonNull
    private UUID id;
    private boolean active = true;
    @NonNull
    private String name;
    private String code;
    private String symbol;
    private short order;

    public static Currency create(@NonNull String name, String code, String symbol, short order) {
        return new Currency(UUID.randomUUID(), true, name, code, symbol, order);
    }

    public void swapOrders(Currency that) {
        short t = that.order;
        that.order = this.order;
        this.order = t;
    }

    @Override
    public String toString() {
        return String.format("Currency[id=%s, name=%s, active=%b, name=%s, code=%s, symbol=%s, order=%d]", id, name, active, name, code, symbol, order);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Currency currency = (Currency) o;
        return Objects.equals(id, currency.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public Currency clone() {
        try {
            return (Currency) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public int compareTo(Currency o) {
        return order - o.getOrder();
    }
}
