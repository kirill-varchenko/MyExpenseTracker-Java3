package com.example.myexpensetracker.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract sealed class Record implements Cloneable permits Income, Exchange, Expense, Transfer {
    @NonNull
    private UUID id;

    private LocalDateTime createdAt;

    @NonNull
    private LocalDate date;

    private String comment;
}
