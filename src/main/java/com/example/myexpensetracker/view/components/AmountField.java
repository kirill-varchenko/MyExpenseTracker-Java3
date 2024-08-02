package com.example.myexpensetracker.view.components;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.shared.HasAllowedCharPattern;
import com.vaadin.flow.component.shared.HasThemeVariant;
import com.vaadin.flow.component.textfield.AbstractNumberField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.function.SerializableFunction;

import java.math.BigDecimal;

/**
 * Input field for decimals with > 0 value and 0.01 step.
 * Tweaked from NumberField.
 */
@Tag("vaadin-number-field")
public class AmountField extends AbstractNumberField<AmountField, BigDecimal> implements HasAllowedCharPattern, HasThemeVariant<TextFieldVariant> {
    public AmountField() {
        this(new Formatter());
        setMin(0.01);
        setStep(0.01);
    }

    private AmountField(Formatter formatter) {
        super(formatter::parse, formatter, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    private static class Formatter implements SerializableFunction<BigDecimal, String> {
        private Formatter() {
        }

        public String apply(BigDecimal valueFromModel) {
            return valueFromModel == null ? "" : valueFromModel.toString();
        }

        private BigDecimal parse(String valueFromClient) {
            return valueFromClient != null && !valueFromClient.isEmpty() ? new BigDecimal(valueFromClient) : null;
        }
    }
}

