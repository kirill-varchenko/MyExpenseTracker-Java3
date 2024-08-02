package com.example.myexpensetracker.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "profile", schema = "public")
public class ProfileEntity {
    @Id
    @Column(name = "user_id", nullable = false)
    private UUID id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "base_currency_id")
    private CurrencyEntity baseCurrency;

    @Setter
    @ManyToOne
    @JoinColumn(name = "default_currency_id")
    private CurrencyEntity defaultCurrency;

    @Setter
    @ManyToOne
    @JoinColumn(name = "default_account_id")
    private AccountEntity defaultAccount;

    public ProfileEntity(UUID id) {
        this.id = id;
    }
}
