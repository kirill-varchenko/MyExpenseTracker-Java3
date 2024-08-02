package com.example.myexpensetracker.domain.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
public class ExchangeRate {
    private BigDecimal directRate;
    private BigDecimal reverseRate;

    public ExchangeRate(BigDecimal directRate) {
        this.directRate = directRate;
        reverseRate = BigDecimal.ONE.divide(directRate, 6, RoundingMode.HALF_DOWN);
    }

    public ExchangeRate(BigDecimal fromAmount, BigDecimal toAmount) {
        this(toAmount.divide(fromAmount, RoundingMode.HALF_DOWN));
    }

    @Override
    public String toString() {
        return String.format("-> %s, <- %s", directRate, reverseRate);
    }
}
