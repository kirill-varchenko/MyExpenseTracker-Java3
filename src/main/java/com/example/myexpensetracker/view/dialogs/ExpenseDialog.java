package com.example.myexpensetracker.view.dialogs;

import com.example.myexpensetracker.domain.model.*;
import com.example.myexpensetracker.view.components.AmountField;
import com.example.myexpensetracker.view.components.IsoDatePicker;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ExpenseDialog extends Dialog {
    @Setter
    private Consumer<Expense> onSave;

    private DatePicker datePicker = new IsoDatePicker();
    private TextField commentField = new TextField(null, "Comment");
    private Button addEntryButton = new Button("Add entry");

    private Select<Account> accountSelect = new Select<>();
    private Select<Currency> currencySelect = new Select<>();
    private Grid<Expense.Entry> entryGrid = new Grid<>();

    private TextField totalField = new TextField("Total");

    private Binder<Expense> binder;

    private List<Expense.Entry> entries = new ArrayList<>();

    private Map<Expense.Entry, AmountField> entryAmountFieldMap = new HashMap<>();

    private Context context;

    public ExpenseDialog(Context context) {
        this.context = context;
        setResizable(true);
        setDraggable(true);
        setCloseOnOutsideClick(false);
        setWidth("1000px");

        accountSelect.setLabel("Account");
        accountSelect.setItems(context.accounts());
        accountSelect.setItemLabelGenerator(Account::getName);
        accountSelect.setValue(context.profile().getDefaultAccount() != null ? context.profile().getDefaultAccount() : context.accounts().getFirst());

        currencySelect.setLabel("Currency");
        currencySelect.setItems(context.currencies());
        currencySelect.setItemLabelGenerator(Currency::getSymbol);
        currencySelect.setValue(context.profile().getDefaultCurrency() != null ? context.profile().getDefaultCurrency() : context.currencies().getFirst());
        currencySelect.addValueChangeListener(event -> updateTotal());

        totalField.setReadOnly(true);

        binder = new Binder<>(Expense.class);
        binder.forField(datePicker).asRequired().bind("date");
        binder.forField(commentField).bind("comment");
        binder.forField(accountSelect).asRequired().bind("account");
        binder.forField(currencySelect).asRequired().bind("currency");

        HorizontalLayout topBar = new HorizontalLayout(datePicker, commentField);
        HorizontalLayout selectors = new HorizontalLayout(accountSelect, currencySelect);
        HorizontalLayout middleBar = new HorizontalLayout(selectors, addEntryButton, totalField);
        middleBar.setAlignItems(FlexComponent.Alignment.END);
        middleBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        addEntryButton.addClickListener(event -> addEntry(makeNewEntry()));

        entryGrid.addColumn(
                new ComponentRenderer<>(AmountField::new, (amountField, entry) -> {
                    amountField.setValue(entry.getAmount());
                    amountField.addValueChangeListener(event -> {
                        entry.setAmount(event.getValue());
                        updateTotal();
                    });
                    entryAmountFieldMap.put(entry, amountField);
                })).setHeader("Amount");
        entryGrid.addColumn(
                new ComponentRenderer<>(ComboBox<Category>::new, (select, entry) -> {
                    select.setItems(context.categories());
                    select.setItemLabelGenerator(Category::getName);
                    select.setValue(entry.getCategory());
                    select.addValueChangeListener(event -> entry.setCategory(event.getValue()));
                })).setHeader("Category");
        entryGrid.addColumn(
                new ComponentRenderer<>(MultiSelectComboBox<Tag>::new, (select, entry) -> {
                    select.setItems(context.tags());
                    select.setValue(entry.getTags());
                    select.setItemLabelGenerator(Tag::getName);
                    select.addValueChangeListener(event -> entry.setTags(event.getValue()));
                })).setHeader("Tags");
        entryGrid.addColumn(
                new ComponentRenderer<>(TextField::new, (textField, entry) -> {
                    textField.setPlaceholder("Comment");
                    textField.setValue(entry.getComment() != null ? entry.getComment() : "");
                    textField.addValueChangeListener(event -> entry.setComment(!event.getValue().isBlank() ? event.getValue() : null));
                })).setHeader("Comment");

        GridContextMenu<Expense.Entry> contextMenu = entryGrid.addContextMenu();
        contextMenu.addItem("Calculate", event -> {
            event.getItem().ifPresent(entry -> {
                new CalculatorDialog(value -> {
                    BigDecimal bigDecimal = BigDecimal.valueOf(value);
                    entry.setAmount(bigDecimal);
                    entryAmountFieldMap.get(entry).setValue(bigDecimal);
                    updateTotal();
                }).open();
            });
        });
        contextMenu.addItem("Subtract", event -> {
            event.getItem().ifPresent(entry -> {
                new CalculatorDialog(value -> {
                    BigDecimal subtrahend = BigDecimal.valueOf(value);
                    entry.setAmount(entry.getAmount().subtract(subtrahend));
                    entryAmountFieldMap.get(entry).setValue(entry.getAmount());
                    addEntry(new Expense.Entry(subtrahend));
                }).open();
            });
        });
        contextMenu.addItem("Delete", event -> {
            event.getItem().ifPresent(entry -> {
                entries.remove(entry);
                entryGrid.getDataProvider().refreshAll();
                updateTotal();
            });
        });

        add(topBar, middleBar, entryGrid);

        Button saveButton = new Button("Save", e -> {
            if (onSave == null || entries.isEmpty()) {
                close();
            }
            if (binder.validate().isOk() && allEntriesValid()) {
                Expense t = binder.getBean();
                t.setEntries(entries);
                onSave.accept(t);
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

    private Expense.Entry makeNewEntry() {
        return new Expense.Entry();
    }

    private boolean allEntriesValid() {
        return entryAmountFieldMap.values().stream().noneMatch(AmountField::isInvalid);
    }

    private void addEntry(Expense.Entry entry) {
        entries.add(entry);
        entryGrid.getDataProvider().refreshAll();
    }

    private void updateTotal() {
        BigDecimal total = entries.stream().map(Expense.Entry::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        Amount amount = new Amount(total, currencySelect.getValue());
        totalField.setValue(amount.toString());
    }

    public void openCreate(Expense expense) {
        setHeaderTitle("Create Expense");
        expense.setAccount(accountSelect.getValue());
        expense.setCurrency(currencySelect.getValue());
        setup(expense);
    }

    public void openUpdate(Expense expense) {
        setHeaderTitle("Edit Expense");
        setup(expense);
    }

    private void setup(Expense expense) {
        entries = expense.getEntries();
        binder.setBean(expense);
        entryGrid.setItems(entries);
        updateTotal();
        open();
    }
}
