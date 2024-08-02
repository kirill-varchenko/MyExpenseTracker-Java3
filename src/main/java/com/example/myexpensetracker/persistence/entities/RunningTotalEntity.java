package com.example.myexpensetracker.persistence.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Immutable
@Table(name = "running_total")
public class RunningTotalEntity {
    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "userId", column = @Column(name = "user_id")),
            @AttributeOverride(name = "currencyId", column = @Column(name = "currency_id")),
            @AttributeOverride(name = "year", column = @Column(name = "year")),
            @AttributeOverride(name = "month", column = @Column(name = "month"))
    })
    private CompositeId id;

    private BigDecimal amount;

    @Data
    public static class CompositeId {
        UUID userId;
        UUID currencyId;

        int year;
        int month;
    }
}
