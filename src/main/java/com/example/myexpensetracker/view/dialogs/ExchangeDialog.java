package com.example.myexpensetracker.view.dialogs;

import com.example.myexpensetracker.domain.model.Account;
import com.example.myexpensetracker.domain.model.Context;
import com.example.myexpensetracker.domain.model.Currency;
import com.example.myexpensetracker.domain.model.Exchange;
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

public class ExchangeDialog extends Dialog {
    @Setter
    private Consumer<Exchange> onSave;

    private DatePicker datePicker = new IsoDatePicker();
    private TextField commentField = new TextField(null, "Comment");

    private Select<Account> accountSelect = new Select<>();
    private Select<Currency> fromCurrencySelect = new Select<>();
    private Select<Currency> toCurrencySelect = new Select<>();
    private AmountField fromAmountField = new AmountField();
    private AmountField toAmountField = new AmountField();

    private Binder<Exchange> binder = new Binder<>(Exchange.class);

    private Context context;

    public ExchangeDialog(Context context) {
        this.context = context;
        setCloseOnOutsideClick(false);

        accountSelect.setLabel("Account");
        accountSelect.setItems(context.accounts());
        accountSelect.setItemLabelGenerator(Account::getName);
        fromAmountField.setLabel("From amount");
        fromCurrencySelect.setItems(context.currencies());
        fromCurrencySelect.setLabel("From currency");
        fromCurrencySelect.setItemLabelGenerator(Currency::getSymbol);
        toAmountField.setLabel("To amount");
        toCurrencySelect.setItems(context.currencies());
        toCurrencySelect.setLabel("To currency");
        toCurrencySelect.setItemLabelGenerator(Currency::getSymbol);

        binder.forField(datePicker).asRequired().bind("date");
        binder.forField(commentField).bind("comment");
        binder.forField(accountSelect).asRequired().bind("account");
        binder.forField(fromAmountField).asRequired().bind(
                exchange -> exchange.getFromAmount().getValue(),
                (exchange, value) -> exchange.getFromAmount().setValue(value)
        );
        binder.forField(fromCurrencySelect).asRequired().bind(
                exchange -> exchange.getFromAmount().getCurrency(),
                (exchange, currency) -> exchange.getFromAmount().setCurrency(currency)
        );
        binder.forField(toAmountField).asRequired().bind(
                exchange -> exchange.getToAmount().getValue(),
                (exchange, value) -> exchange.getToAmount().setValue(value)
        );
        binder.forField(toCurrencySelect).asRequired().bind(
                exchange -> exchange.getToAmount().getCurrency(),
                (exchange, currency) -> exchange.getToAmount().setCurrency(currency)
        );

        HorizontalLayout topBar = new HorizontalLayout(datePicker, commentField);
        HorizontalLayout main = new HorizontalLayout(
                fromAmountField, fromCurrencySelect,
                VaadinIcon.ARROWS_LONG_RIGHT.create(),
                toAmountField, toCurrencySelect);
        main.setAlignItems(FlexComponent.Alignment.CENTER);
        add(topBar, accountSelect, main);

        Button saveButton = new Button("Save", e -> {
            if (onSave == null) {
                close();
            }
            if (binder.validate().isOk() && !fromCurrencySelect.getValue().equals(toCurrencySelect.getValue())) {
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

    public void openCreate(Exchange exchange) {
        setHeaderTitle("Create Exchange");
        setup(exchange);
    }

    public void openUpdate(Exchange exchange) {
        setHeaderTitle("Edit Exchange");
        setup(exchange);
    }

    private void setup(Exchange exchange) {
        binder.setBean(exchange);
        open();
    }
}
