package com.example.myexpensetracker.view.components;

import com.vaadin.flow.component.datepicker.DatePicker;

/**
 * Customized DatePicker with ISO8601 date format and first day Monday.
 */
public class IsoDatePicker extends DatePicker {
    private static final DatePicker.DatePickerI18n singleFormatI18n = new DatePicker.DatePickerI18n();

    static {
        singleFormatI18n.setDateFormat("yyyy-MM-dd");
        singleFormatI18n.setFirstDayOfWeek(1);
    }

    public IsoDatePicker() {
        super();
        setI18n(singleFormatI18n);
    }
}
