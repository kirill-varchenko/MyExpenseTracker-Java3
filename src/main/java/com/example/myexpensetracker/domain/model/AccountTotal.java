package com.example.myexpensetracker.domain.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
public class AccountTotal {
    private Account account;

    private MultiAmount amounts = new MultiAmount();

    private List<AccountTotal> children = new ArrayList<>();

    public AccountTotal(Account account) {
        this.account = account;
    }

    public BigDecimal getAmount(Currency currency) {
        return amounts.getAmount(currency);
    }

    public String getAccountName() {
        return account.getName();
    }

    @Override
    public String toString() {
        return String.format("AccountTotal[account=%s, amounts=[%s]]", account, amounts);
    }

}
