package com.example.myexpensetracker.view.charts;


import com.example.myexpensetracker.domain.model.MonthlyExpenseIncome;
import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.ApexChartsBuilder;
import com.github.appreciated.apexcharts.config.builder.*;
import com.github.appreciated.apexcharts.config.chart.Type;
import com.github.appreciated.apexcharts.config.chart.builder.EventsBuilder;
import com.github.appreciated.apexcharts.config.plotoptions.bar.builder.ColorsBuilder;
import com.github.appreciated.apexcharts.config.plotoptions.bar.builder.RangesBuilder;
import com.github.appreciated.apexcharts.config.plotoptions.builder.BarBuilder;
import com.github.appreciated.apexcharts.helper.Series;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.*;

// Adapted from:
// https://github.com/appreciated/apexcharts-flow/blob/master/src/test/java/com/github/appreciated/apexcharts/examples/bar/RangedVerticalBarChartExample.java

public class MonthlyExpenseIncomeChartBuilder extends ApexChartsBuilder {
    private List<Series<BigDecimal>> seriesList = new ArrayList<>();

    public MonthlyExpenseIncomeChartBuilder() {
        withChart(ChartBuilder.get()
                .withType(Type.BAR)
                .build())
                // .withColors() // Empty call makes chart to not render
                .withPlotOptions(PlotOptionsBuilder.get()
                        .withBar(BarBuilder.get()
                                .withHorizontal(false)
                                .withColumnWidth("55%")
                                .withColors(ColorsBuilder.get()
                                        // Ranges overwrite colors in range for all series
                                        .withRanges(RangesBuilder.get()
                                                .withFrom(50d)
                                                .withTo(75d)
                                                .withColor("#3d923d")
                                                .build(), RangesBuilder.get()
                                                .withFrom(75d)
                                                .withTo(100d)
                                                .withColor("#88593e")
                                                .build())
                                        .build())
                                .build())
                        .build())
                .withDataLabels(DataLabelsBuilder.get()
                        .withEnabled(false).build())
                .withStroke(StrokeBuilder.get()
                        .withShow(true)
                        .withWidth(2.0)
                        .withColors("transparent")
                        .build())
                .withFill(FillBuilder.get()
                        .withOpacity(1.0).build());
    }

    public MonthlyExpenseIncomeChartBuilder withSeries(String name, BigDecimal[] data) {
        seriesList.add(new Series<>(name, data));
        return this;
    }

    public MonthlyExpenseIncomeChartBuilder withCategories(String[] categories) {
        withXaxis(XAxisBuilder.get().withCategories(categories).build());
        return this;
    }

    public ApexCharts build(List<MonthlyExpenseIncome> monthlyExpenseIncomes) {
        Set<YearMonth> yearMonths = new HashSet<>();
        Map<TypeCurrency, Map<YearMonth, BigDecimal>> map = new HashMap<>();

        monthlyExpenseIncomes.forEach(monthly -> {
            yearMonths.add(monthly.getYearMonth());
            monthly.getExpenses().getAmountStream().forEach(amount -> {
                TypeCurrency typeCurrency = new TypeCurrency("Expense", amount.getCurrency().getSymbol());
                Map<YearMonth, BigDecimal> subMap = map.containsKey(typeCurrency) ? map.get(typeCurrency) : new HashMap<>();
                subMap.put(monthly.getYearMonth(), amount.getValue());
                map.put(typeCurrency, subMap);
            });
            monthly.getIncomes().getAmountStream().forEach(amount -> {
                TypeCurrency typeCurrency = new TypeCurrency("Income", amount.getCurrency().getSymbol());
                Map<YearMonth, BigDecimal> subMap = map.containsKey(typeCurrency) ? map.get(typeCurrency) : new HashMap<>();
                subMap.put(monthly.getYearMonth(), amount.getValue());
                map.put(typeCurrency, subMap);
            });
        });
        List<YearMonth> yearMonthList = yearMonths.stream().sorted().toList();

        map.forEach((key, value) -> {
            BigDecimal[] values = yearMonthList.stream().map(yearMonth -> value.getOrDefault(yearMonth, BigDecimal.ZERO)).toArray(BigDecimal[]::new);
            String name = key.toString();
            withSeries(name, values);
        });

        withCategories(yearMonthList.stream().map(YearMonth::toString).toArray(String[]::new));

        super.withSeries(seriesList.toArray(Series[]::new));
        return super.build();
    }

    record TypeCurrency(String type, String currency) {
        @Override
        public String toString() {
            return String.format("%s (%s)", type, currency);
        }
    }
}
