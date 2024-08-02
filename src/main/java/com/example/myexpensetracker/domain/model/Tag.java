package com.example.myexpensetracker.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
public class Tag implements Cloneable {
    @NonNull
    private UUID id;
    private boolean active = true;
    @NonNull
    private String name;

    public static Tag create(String name) {
        return new Tag(UUID.randomUUID(), true, name);
    }

    @Override
    public String toString() {
        return String.format("Tag[id=%s, name=%s, active=%b]", id, name, active);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(id, tag.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public Tag clone() {
        try {
            return (Tag) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
