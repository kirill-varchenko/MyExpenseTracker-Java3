package com.example.myexpensetracker.domain;

import com.example.myexpensetracker.domain.model.Account;
import com.example.myexpensetracker.domain.model.Category;
import com.example.myexpensetracker.domain.model.Currency;
import com.example.myexpensetracker.domain.model.Tag;

import java.time.LocalDate;

public record RecordFilter(
        LocalDate afterDate,
        LocalDate beforeDate,
        RecordType recordType,
        Account account,
        Category category,
        Currency currency,
        Tag tag
) {
}
