package com.example.myexpensetracker.domain.model;

import lombok.Getter;

import java.time.YearMonth;

@Getter
public class MonthlyExpenseIncome implements Comparable<MonthlyExpenseIncome> {
    private YearMonth yearMonth;
    private MultiAmount incomes = new MultiAmount();
    private MultiAmount expenses = new MultiAmount();

    public MonthlyExpenseIncome(YearMonth yearMonth) {
        this.yearMonth = yearMonth;
    }

    @Override
    public String toString() {
        return String.format("MonthlyExpenseIncome[yearMonth=%s, incomes=[%s], expenses=[%s]]", yearMonth, incomes, expenses);
    }

    @Override
    public int compareTo(MonthlyExpenseIncome o) {
        return yearMonth.compareTo(o.yearMonth);
    }
}
