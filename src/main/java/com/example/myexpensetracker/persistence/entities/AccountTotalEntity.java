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
@Table(name = "account_totals")
public class AccountTotalEntity {
    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "userId", column = @Column(name = "user_id")),
            @AttributeOverride(name = "currencyId", column = @Column(name = "currency_id")),
            @AttributeOverride(name = "accountId", column = @Column(name = "account_id"))
    })
    private CompositeId id;
    private BigDecimal amount;

    @Data
    public static class CompositeId {
        private UUID userId;
        private UUID currencyId;
        private UUID accountId;
    }
}
