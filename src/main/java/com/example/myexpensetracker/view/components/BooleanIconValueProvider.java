package com.example.myexpensetracker.view.components;

import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.function.ValueProvider;

import java.util.function.Function;

/**
 * Provides boolean values as Icons V/X.
 *
 * @param <T> Source object type.
 */
public class BooleanIconValueProvider<T> implements ValueProvider<T, Icon> {
    private Function<T, Boolean> getter;

    public BooleanIconValueProvider(Function<T, Boolean> getter) {
        this.getter = getter;
    }

    @Override
    public Icon apply(T t) {
        Icon icon;
        if (getter.apply(t)) {
            icon = VaadinIcon.CHECK.create();
            icon.setColor("green");
        } else {
            icon = VaadinIcon.CLOSE_SMALL.create();
            icon.setColor("gray");
        }
        return icon;
    }
}
