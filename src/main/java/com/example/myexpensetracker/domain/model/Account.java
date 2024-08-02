package com.example.myexpensetracker.domain.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class Account implements Cloneable {
    @NonNull
    private UUID id;
    private boolean active = true;
    @NonNull
    private String name;
    @NonNull
    private Type type;
    private Account parent;

    private List<Account> children = new ArrayList<>();

    public Account(UUID id, boolean active, Account parent, String name, Type type) {
        this.id = id;
        this.active = active;
        this.parent = parent;
        this.name = name;
        this.type = type;
    }

    public static Account create(@NonNull String name, @NonNull Type type, Account parent) {
        return new Account(UUID.randomUUID(), true, parent, name, type);
    }


    public enum Type {
        CASH, DEBT, BANK, CRYPTO;
    }

    @Override
    public String toString() {
        return String.format("Account[id=%s, name=%s, type=%s, active=%b, parent=%s]", id, name, type, active, parent != null ? parent.getName() : null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public Account clone() {
        try {
            return (Account) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
