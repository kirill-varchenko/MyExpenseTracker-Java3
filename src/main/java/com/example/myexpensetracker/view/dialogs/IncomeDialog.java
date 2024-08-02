package com.example.myexpensetracker.view.dialogs;

import com.example.myexpensetracker.domain.model.*;
import com.example.myexpensetracker.view.components.AmountField;
import com.example.myexpensetracker.view.components.IsoDatePicker;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import lombok.Setter;

import java.util.function.Consumer;

public class IncomeDialog extends Dialog {
    @Setter
    private Consumer<Income> onSave;

    private DatePicker datePicker = new IsoDatePicker();
    private TextField commentField = new TextField(null, "Comment");

    private Select<Account> accountSelect = new Select<>();
    private AmountField amountField = new AmountField();
    private Select<Currency> currencySelect = new Select<>();
    private Select<Category> categorySelect = new Select<>();
    private MultiSelectComboBox<Tag> tagSelect = new MultiSelectComboBox<>();

    private Binder<Income> binder = new Binder<>(Income.class);

    private Context context;

    public IncomeDialog(Context context) {
        this.context = context;
        setCloseOnOutsideClick(false);

        accountSelect.setLabel("Account");
        accountSelect.setItems(context.accounts());
        accountSelect.setItemLabelGenerator(Account::getName);
        amountField.setLabel("Amount");
        currencySelect.setItems(context.currencies());
        currencySelect.setLabel("Currency");
        currencySelect.setItemLabelGenerator(Currency::getSymbol);
        categorySelect.setItems(context.categories());
        categorySelect.setLabel("Category");
        categorySelect.setEmptySelectionAllowed(true);
        categorySelect.setEmptySelectionCaption("<None>");
        categorySelect.setItemLabelGenerator(category -> category != null ? category.getName() : "<None>");
        tagSelect.setLabel("Tags");
        tagSelect.setItems(context.tags());
        tagSelect.setItemLabelGenerator(Tag::getName);

        binder.forField(datePicker).asRequired().bind("date");
        binder.forField(commentField).bind("comment");
        binder.forField(accountSelect).asRequired().bind("account");
        binder.forField(amountField).asRequired().bind(
                income -> income.getAmount().getValue(),
                (income, value) -> income.getAmount().setValue(value)
        );
        binder.forField(currencySelect).asRequired().bind(
                income -> income.getAmount().getCurrency(),
                (income, currency) -> income.getAmount().setCurrency(currency)
        );
        binder.forField(categorySelect).bind("category");
        binder.forField(tagSelect).bind("tags");

        HorizontalLayout topBar = new HorizontalLayout(datePicker, commentField);
        HorizontalLayout main = new HorizontalLayout(
                accountSelect, amountField, currencySelect, categorySelect, tagSelect);
        main.setAlignItems(FlexComponent.Alignment.CENTER);
        add(topBar, accountSelect, main);

        Button saveButton = new Button("Save", e -> {
            if (onSave == null) {
                close();
            }
            if (binder.validate().isOk()) {
                onSave.accept(binder.getBean());
                close();
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelButton = new Button("Cancel", e -> {
            close();
        });
        getFooter().add(cancelButton);
        getFooter().add(saveButton);
    }

    public void openCreate(Income income) {
        setHeaderTitle("Create Income");
        setup(income);
    }

    public void openUpdate(Income income) {
        setHeaderTitle("Edit Income");
        setup(income);
    }

    private void setup(Income income) {
        binder.setBean(income);
        open();
    }
}
