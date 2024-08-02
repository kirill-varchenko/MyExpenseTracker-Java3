package com.example.myexpensetracker.domain.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Setter
@Getter
public class Category implements Cloneable {
    @NonNull
    private UUID id;
    private boolean active = true;
    @NonNull
    private String name;
    private Category parent;

    private List<Category> children = new ArrayList<>();

    public Category(@NonNull UUID id, boolean active, @NonNull String name, Category parent) {
        this.id = id;
        this.active = active;
        this.name = name;
        this.parent = parent;
        if (parent != null) {
            parent.getChildren().add(this);
        }
    }

    public static Category create(String name, Category parent) {
        return new Category(UUID.randomUUID(), true, name, parent);
    }

    @Override
    public String toString() {
        return String.format("Category[id=%s, name=%s, active=%b, parent=%s]", id, name, active, parent != null ? parent.getName() : null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public Category clone() {
        try {
            return (Category) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
