package com.example.myexpensetracker.view.dialogs;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import lombok.Setter;

import java.util.function.Consumer;

public class SimpleCreateUpdateDialog<T> extends Dialog {
    @Setter
    private Consumer<T> onSave;

    private final Class<T> clazz;

    private final VerticalLayout dialogLayout = new VerticalLayout();
    protected Binder<T> binder;

    public SimpleCreateUpdateDialog(Class<T> clazz) {
        this.clazz = clazz;
        binder = new Binder<>(clazz);
        super.add(dialogLayout);
        Button saveButton = new Button("Save", e -> {
            if (onSave != null && binder.validate().isOk()) {
                onSave.accept(binder.getBean());
            }
            close();
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelButton = new Button("Cancel", e -> {
            close();
        });
        getFooter().add(cancelButton);
        getFooter().add(saveButton);
    }

    @Override
    public void add(Component... components) {
        dialogLayout.add(components);
    }

    public void openCreate(T t) {
        setHeaderTitle("Create " + clazz.getSimpleName());
        binder.setBean(t);
        initCreate(t);
        open();
    }

    public void openUpdate(T t) {
        setHeaderTitle("Edit " + clazz.getSimpleName());
        binder.setBean(t);
        initUpdate(t);
        open();
    }

    protected void initCreate(T t) {
    }

    protected void initUpdate(T t) {
    }
}
