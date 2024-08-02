package com.example.myexpensetracker.view.dialogs;

import com.example.myexpensetracker.domain.model.Account;
import com.example.myexpensetracker.domain.model.Context;
import com.example.myexpensetracker.domain.model.Currency;
import com.example.myexpensetracker.domain.model.Transfer;
import com.example.myexpensetracker.view.components.AmountField;
import com.example.myexpensetracker.view.components.IsoDatePicker;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import lombok.Setter;

import java.util.function.Consumer;

public class TransferDialog extends Dialog {
    @Setter
    private Consumer<Transfer> onSave;

    private DatePicker datePicker = new IsoDatePicker();
    private TextField commentField = new TextField(null, "Comment");

    private AmountField amountField = new AmountField();
    private Select<Currency> currencySelect = new Select<>();
    private Select<Account> fromAccountSelect = new Select<>();
    private Select<Account> toAccountSelect = new Select<>();

    private Binder<Transfer> binder = new Binder<>(Transfer.class);

    private Context context;

    public TransferDialog(Context context) {
        this.context = context;
        setCloseOnOutsideClick(false);

        amountField.setLabel("Amount");
        currencySelect.setItems(context.currencies());
        currencySelect.setLabel("Currency");
        currencySelect.setItemLabelGenerator(Currency::getSymbol);
        fromAccountSelect.setLabel("From account");
        fromAccountSelect.setItems(context.accounts());
        fromAccountSelect.setItemLabelGenerator(Account::getName);
        toAccountSelect.setLabel("To account");
        toAccountSelect.setItems(context.accounts());
        toAccountSelect.setItemLabelGenerator(Account::getName);


        binder.forField(datePicker).asRequired().bind("date");
        binder.forField(commentField).bind("comment");
        binder.forField(amountField).asRequired().bind(
                transfer -> transfer.getAmount().getValue(),
                (transfer, value) -> transfer.getAmount().setValue(value)
        );
        binder.forField(currencySelect).asRequired().bind(
                transfer -> transfer.getAmount().getCurrency(),
                (transfer, currency) -> transfer.getAmount().setCurrency(currency)
        );
        binder.forField(fromAccountSelect).asRequired().bind("fromAccount");
        binder.forField(toAccountSelect).asRequired().bind("toAccount");

        HorizontalLayout topBar = new HorizontalLayout(datePicker, commentField);
        HorizontalLayout amount = new HorizontalLayout(amountField, currencySelect);
        HorizontalLayout main = new HorizontalLayout(
                fromAccountSelect,
                VaadinIcon.ARROWS_LONG_RIGHT.create(),
                toAccountSelect);
        main.setAlignItems(FlexComponent.Alignment.CENTER);
        add(topBar, amount, main);

        Button saveButton = new Button("Save", e -> {
            if (onSave == null) {
                close();
            }
            if (binder.validate().isOk() && !fromAccountSelect.getValue().equals(toAccountSelect.getValue())) {
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

    public void openCreate(Transfer transfer) {
        setHeaderTitle("Create Transfer");
        setup(transfer);
    }

    public void openUpdate(Transfer transfer) {
        setHeaderTitle("Edit Transfer");
        setup(transfer);
    }

    private void setup(Transfer transfer) {
        binder.setBean(transfer);
        open();
    }
}
