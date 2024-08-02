package com.example.myexpensetracker.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Profile {
    private Currency baseCurrency;
    private Currency defaultCurrency;
    private Account defaultAccount;
}
