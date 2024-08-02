package com.example.myexpensetracker.persistence.mappers;

import com.example.myexpensetracker.domain.model.Currency;
import com.example.myexpensetracker.domain.model.*;
import com.example.myexpensetracker.persistence.entities.AccountTotalEntity;
import com.example.myexpensetracker.persistence.entities.MonthlyExpenseIncomeEntity;
import com.example.myexpensetracker.persistence.entities.RunningTotalEntity;
import lombok.extern.log4j.Log4j2;

import java.time.YearMonth;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
public class SummaryMapper {
    public static List<AccountTotal> mapAccountTotals(List<AccountTotalEntity> entities, Context context) {
        Map<UUID, Currency> currencies = context.currencies().stream().collect(Collectors.toMap(Currency::getId, Function.identity()));
        Map<UUID, List<AccountTotalEntity>> entityGroups = entities.stream()
                .collect(Collectors.groupingBy(entity -> entity.getId().getAccountId()));

        Map<UUID, AccountTotal> totals = new HashMap<>();

        context.accounts().forEach(account -> {
            if (account.isActive()) {
                AccountTotal accountTotal = new AccountTotal(account);
                if (entityGroups.containsKey(account.getId())) {
                    List<AccountTotalEntity> accountTotalEntityList = entityGroups.get(account.getId());
                    accountTotalEntityList.forEach(entity -> {
                        Amount amount = new Amount(entity.getAmount(), currencies.get(entity.getId().getCurrencyId()));
                        accountTotal.getAmounts().add(amount);
                    });
                }
                totals.put(account.getId(), accountTotal);
            }
        });

        List<AccountTotal> roots = new ArrayList<>();
        totals.values().forEach(total -> {
            if (total.getAccount().getParent() == null) {
                roots.add(total);
            } else {
                AccountTotal parent = totals.get(total.getAccount().getParent().getId());
                parent.getChildren().add(total);
            }
        });
        return roots;
    }

    public static List<MonthlyExpenseIncome> mapMonthlyExpenseIncomes(List<MonthlyExpenseIncomeEntity> entities, Context context) {
        Map<UUID, Currency> currencies = context.currencies().stream().collect(Collectors.toMap(Currency::getId, Function.identity()));
        Map<YearMonth, MonthlyExpenseIncome> map = new HashMap<>();

        entities.forEach(entity -> {
            YearMonth yearMonth = YearMonth.of(entity.getId().getYear().intValue(), entity.getId().getMonth().intValue());
            MonthlyExpenseIncome monthlyExpenseIncome = map.getOrDefault(yearMonth, new MonthlyExpenseIncome(yearMonth));
            Currency currency = currencies.get(entity.getId().getCurrencyId());
            Amount amount = new Amount(entity.getAmount(), currency);
            switch (entity.getId().getType()) {
                case INCOME -> monthlyExpenseIncome.getIncomes().add(amount);
                case EXPENSE -> monthlyExpenseIncome.getExpenses().add(amount.negate());
                default -> log.warn("Unexpected type in MonthlyExpenseIncomeEntity: {}", entity.getId());
            }
            map.put(yearMonth, monthlyExpenseIncome);
        });
        return map.values().stream().sorted(MonthlyExpenseIncome::compareTo).toList().reversed();
    }

    public static List<RunningTotal> mapRunningTotals(List<RunningTotalEntity> entities, Context context) {
        Map<UUID, Currency> currencies = context.currencies().stream().collect(Collectors.toMap(Currency::getId, Function.identity()));
        Map<YearMonth, RunningTotal> map = new HashMap<>();

        entities.forEach(entity -> {
            YearMonth yearMonth = YearMonth.of(entity.getId().getYear(), entity.getId().getMonth());
            RunningTotal runningTotal = map.getOrDefault(yearMonth, new RunningTotal(yearMonth));
            Currency currency = currencies.get(entity.getId().getCurrencyId());
            Amount amount = new Amount(entity.getAmount(), currency);
            runningTotal.getTotal().add(amount);
            map.put(yearMonth, runningTotal);
        });
        return map.values().stream().sorted(RunningTotal::compareTo).toList().reversed();
    }
}
