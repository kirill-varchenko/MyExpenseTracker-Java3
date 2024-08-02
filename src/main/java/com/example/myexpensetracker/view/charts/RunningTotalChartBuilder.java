package com.example.myexpensetracker.view.charts;

import com.example.myexpensetracker.domain.model.RunningTotal;
import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.ApexChartsBuilder;
import com.github.appreciated.apexcharts.config.YAxis;
import com.github.appreciated.apexcharts.config.builder.*;
import com.github.appreciated.apexcharts.config.chart.Type;
import com.github.appreciated.apexcharts.config.chart.builder.ZoomBuilder;
import com.github.appreciated.apexcharts.config.grid.builder.RowBuilder;
import com.github.appreciated.apexcharts.config.stroke.Curve;
import com.github.appreciated.apexcharts.config.yaxis.builder.AxisBorderBuilder;
import com.github.appreciated.apexcharts.config.yaxis.builder.TitleBuilder;
import com.github.appreciated.apexcharts.helper.Series;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.*;

public class RunningTotalChartBuilder extends ApexChartsBuilder {
    private List<Series<BigDecimal>> seriesList = new ArrayList<>();

    public RunningTotalChartBuilder() {
        withChart(ChartBuilder.get()
                .withType(Type.LINE)
                .withZoom(ZoomBuilder.get()
                        .withEnabled(false)
                        .build())
                .build())
                .withStroke(StrokeBuilder.get()
                        .withCurve(Curve.STRAIGHT)
                        .withWidthArray(List.of(6.0, 3.0))
                        .build())
                .withGrid(GridBuilder.get()
                        .withRow(RowBuilder.get()
                                .withColors("#f3f3f3", "transparent")
                                .withOpacity(0.5).build()
                        ).build());
    }

    public RunningTotalChartBuilder withSeries(String name, BigDecimal[] data) {
        seriesList.add(new Series<>(name, data));
        return this;
    }

    public RunningTotalChartBuilder withCategories(String[] categories) {
        withXaxis(XAxisBuilder.get().withCategories(categories).build());
        return this;
    }

    @Override
    public ApexCharts build() {
        YAxis[] yAxes = seriesList.stream().map(series -> YAxisBuilder.get()
                .withTitle(TitleBuilder.get().withText(series.getName()).build())
                .withAxisBorder(AxisBorderBuilder.get().withShow(true).build())
                .withOpposite(true)
                .build()).toArray(YAxis[]::new);
        withYaxis(yAxes);
        super.withSeries(seriesList.toArray(Series[]::new));

        return super.build();
    }


    public ApexCharts build(List<RunningTotal> runningTotals) {
        Set<YearMonth> yearMonths = new HashSet<>();
        Map<String, Map<YearMonth, BigDecimal>> map = new HashMap<>();

        runningTotals.forEach(runningTotal -> {
            yearMonths.add(runningTotal.getYearMonth());
            runningTotal.getTotal().getAmountStream().forEach(amount -> {
                String currency = amount.getCurrency().getSymbol();
                Map<YearMonth, BigDecimal> subMap = map.containsKey(currency) ? map.get(currency) : new HashMap<>();
                subMap.put(runningTotal.getYearMonth(), amount.getValue());
                map.put(currency, subMap);
            });
        });

        List<YearMonth> yearMonthList = yearMonths.stream().sorted().toList();

        map.forEach((currency, sumMap) -> {
            BigDecimal prev = BigDecimal.ZERO;
            List<BigDecimal> values = new ArrayList<>();
            for (YearMonth yearMonth : yearMonthList) {
                BigDecimal value = sumMap.getOrDefault(yearMonth, prev);
                values.add(value);
                prev = value;
            }
            withSeries(currency, values.toArray(BigDecimal[]::new));
        });

        withCategories(yearMonthList.stream().map(YearMonth::toString).toArray(String[]::new));

        return build();
    }
}
