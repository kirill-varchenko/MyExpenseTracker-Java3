package com.example.myexpensetracker.view;

import com.example.myexpensetracker.domain.model.AccountTotal;
import com.example.myexpensetracker.domain.model.Currency;
import com.example.myexpensetracker.domain.model.MonthlyExpenseIncome;
import com.example.myexpensetracker.domain.model.RunningTotal;
import com.example.myexpensetracker.services.ContextService;
import com.example.myexpensetracker.services.SummaryService;
import com.example.myexpensetracker.view.charts.MonthlyExpenseIncomeChartBuilder;
import com.example.myexpensetracker.view.charts.RunningTotalChartBuilder;
import com.github.appreciated.apexcharts.ApexCharts;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route(value = "/dashboard", layout = MainView.class)
@PermitAll
public class DashboardView extends FormLayout {
    private TreeGrid<AccountTotal> accountTotalTreeGrid = new TreeGrid<>();
    private Grid<MonthlyExpenseIncome> monthlyExpenseIncomeGrid = new Grid<>();
    private ApexCharts monthlyExpenseIncomeChart;
    private ApexCharts runningTotalChart;

    @Autowired
    public DashboardView(SummaryService summaryService, ContextService contextService) {
        List<AccountTotal> accountTotals = summaryService.getAccountTotals();
        List<MonthlyExpenseIncome> monthlyExpenseIncomes = summaryService.getMonthlyExpenseIncomes();
        List<RunningTotal> runningTotals = summaryService.getRunningTotals();

        accountTotalTreeGrid.addHierarchyColumn(AccountTotal::getAccountName).setHeader("Account");
        accountTotalTreeGrid.setItems(accountTotals, AccountTotal::getChildren);
        accountTotalTreeGrid.expand(accountTotals);
        accountTotalTreeGrid.setHeight("300px");

        for (Currency currency : contextService.get().getActiveCurrencies()) {
            accountTotalTreeGrid.addColumn(total -> total.getAmount(currency)).setHeader(currency.getCode());
        }

        monthlyExpenseIncomeGrid.addColumn(MonthlyExpenseIncome::getYearMonth).setHeader("Month");
        monthlyExpenseIncomeGrid.addColumn(MonthlyExpenseIncome::getExpenses).setHeader("Expenses");
        monthlyExpenseIncomeGrid.addColumn(MonthlyExpenseIncome::getIncomes).setHeader("Incomes");
        monthlyExpenseIncomeGrid.setItems(monthlyExpenseIncomes);
        monthlyExpenseIncomeGrid.setHeight("300px");

        monthlyExpenseIncomeChart = new MonthlyExpenseIncomeChartBuilder().build(monthlyExpenseIncomes);
        runningTotalChart = new RunningTotalChartBuilder().build(runningTotals);

        add(accountTotalTreeGrid, monthlyExpenseIncomeGrid, monthlyExpenseIncomeChart, runningTotalChart);
    }
}
