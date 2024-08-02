package com.example.myexpensetracker.domain.model;

import lombok.Getter;

import java.time.YearMonth;

@Getter
public class RunningTotal implements Comparable<RunningTotal> {
    private YearMonth yearMonth;
    private MultiAmount total = new MultiAmount();

    public RunningTotal(YearMonth yearMonth) {
        this.yearMonth = yearMonth;
    }

    @Override
    public String toString() {
        return String.format("RunningTotal[yearMonth=%s, total=[%s]]", yearMonth, total);
    }

    @Override
    public int compareTo(RunningTotal o) {
        return yearMonth.compareTo(o.yearMonth);
    }
}
