package com.example.myexpensetracker.view.components;

import com.example.myexpensetracker.domain.model.Record;
import com.example.myexpensetracker.domain.model.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.function.ValueProvider;

/**
 * Provides Icon representation of records
 */
public class RecordIconValueProvider implements ValueProvider<Record, Icon> {
    @Override
    public Icon apply(Record record) {
        return switch (record) {
            case Expense ex -> {
                Icon icon = VaadinIcon.MONEY_WITHDRAW.create();
                icon.setColor("red");
                yield icon;
            }
            case Income in -> {
                Icon icon = VaadinIcon.MONEY_DEPOSIT.create();
                icon.setColor("green");
                yield icon;
            }
            case Exchange exc -> {
                Icon icon = VaadinIcon.MONEY_EXCHANGE.create();
                icon.setColor("blue");
                yield icon;
            }
            case Transfer tr -> {
                Icon icon = VaadinIcon.EXCHANGE.create();
                icon.setColor("magenta");
                yield icon;
            }
        };
    }
}
