package com.example.myexpensetracker.persistence.entities;

import com.example.myexpensetracker.domain.RecordType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Immutable
@Table(name = "monthly_expense_income")
public class MonthlyExpenseIncomeEntity {
    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "userId", column = @Column(name = "user_id")),
            @AttributeOverride(name = "currencyId", column = @Column(name = "currency_id")),
            @AttributeOverride(name = "type", column = @Column(name = "type")),
            @AttributeOverride(name = "year", column = @Column(name = "year")),
            @AttributeOverride(name = "month", column = @Column(name = "month"))
    })
    private CompositeId id;

    private BigDecimal amount;

    @Data
    public static class CompositeId {
        UUID userId;
        UUID currencyId;

        @Enumerated
        @JdbcType(PostgreSQLEnumJdbcType.class)
        RecordType type;

        // TODO: fix types in DB View (NUMERIC -> INTEGER)
        BigDecimal year;
        BigDecimal month;
    }
}
