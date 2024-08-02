package com.example.myexpensetracker.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "currency", schema = "public")
public class CurrencyEntity {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private boolean active = true;

    @Column(length = 255, nullable = false)
    private String name;

    @Column(length = 4)
    private String code;

    @Column(length = 1)
    private String symbol;

    @Column(name = "\"order\"")
    private short order;

}
