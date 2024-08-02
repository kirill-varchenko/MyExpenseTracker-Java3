package com.example.myexpensetracker.view;

import com.example.myexpensetracker.domain.RecordFilter;
import com.example.myexpensetracker.domain.RecordType;
import com.example.myexpensetracker.domain.model.Record;
import com.example.myexpensetracker.domain.model.*;
import com.example.myexpensetracker.services.ContextService;
import com.example.myexpensetracker.services.RecordService;
import com.example.myexpensetracker.view.components.IsoDatePicker;
import com.example.myexpensetracker.view.components.RecordIconValueProvider;
import com.example.myexpensetracker.view.dialogs.ExchangeDialog;
import com.example.myexpensetracker.view.dialogs.ExpenseDialog;
import com.example.myexpensetracker.view.dialogs.IncomeDialog;
import com.example.myexpensetracker.view.dialogs.TransferDialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridSubMenu;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Log4j2
@Route(value = "records", layout = MainView.class)
@PermitAll
public class RecordView extends VerticalLayout {
    private RecordService recordService;

    private IsoDatePicker afterDatePicker = new IsoDatePicker();
    private IsoDatePicker beforeDatePicker = new IsoDatePicker();
    private Select<RecordType> recordTypeSelect = new Select<>();
    private Select<Account> accountSelect = new Select<>();
    private ComboBox<Category> categorySelect = new ComboBox<>();
    private Select<Currency> currencySelect = new Select<>();
    private Select<Tag> tagSelect = new Select<>();

    private Grid<Record> recordList = new Grid<>();

    private IntegerField totalField = new IntegerField("Total");
    private IntegerField pageField = new IntegerField("Page");
    private IntegerField pagesField = new IntegerField("Pages");
    private Button prevPageButton = new Button(VaadinIcon.ANGLE_LEFT.create());
    private Button nextPageButton = new Button(VaadinIcon.ANGLE_RIGHT.create());
    private Button firstPageButton = new Button(VaadinIcon.ANGLE_DOUBLE_LEFT.create());
    private Button lastPageButton = new Button(VaadinIcon.ANGLE_DOUBLE_RIGHT.create());

    private int currentPage = 1;
    private int pageSize = 20;
    private int totalPages = 0;
    private int totalItems = 0;

    private RecordFilter filter = null;
    private Context context;

    @Autowired
    public RecordView(RecordService recordService, ContextService contextService) {
        this.recordService = recordService;
        context = contextService.get();

        setupRecordList();

        HorizontalLayout filterBar = createFilterBar();

        HorizontalLayout paginationBar = createPaginationBar();

        add(filterBar, recordList, paginationBar);

        loadItems();
    }

    private void setupRecordList() {
        recordList.setHeight("600px");
        recordList.addColumn(Record::getDate).setHeader("Date");
        recordList.addComponentColumn(new RecordIconValueProvider())
                .setTooltipGenerator(RecordView::getRecordTypeTooltip)
                .setHeader("Type");
        recordList.addColumn(RecordView::getRecordAccounts).setHeader("Accounts");
        recordList.addColumn(RecordView::getRecordAmount).setHeader("Amount");
        recordList.addColumn(Record::getComment).setHeader("Comment");
        recordList.addColumn(RecordView::getRecordTags).setHeader("Tags");

        GridContextMenu<Record> contextMenu = recordList.addContextMenu();
        GridSubMenu<Record> addMenu = contextMenu.addItem("Add").getSubMenu();
        addMenu.addItem("Expense", event -> {
            Expense expense = Expense.create(LocalDate.now(), null);
            ExpenseDialog dialog = new ExpenseDialog(context);
            dialog.setOnSave(record -> {
                recordService.save(record);
                loadItems();
            });
            dialog.openCreate(expense);
        });
        addMenu.addItem("Income", event -> {
            Income income = Income.create(LocalDate.now(), null);
            IncomeDialog dialog = new IncomeDialog(context);
            dialog.setOnSave(record -> {
                recordService.save(record);
                loadItems();
            });
            dialog.openCreate(income);
        });
        addMenu.addItem("Exchange", event -> {
            Exchange exchange = Exchange.create();
            ExchangeDialog exchangeDialog = new ExchangeDialog(context);
            exchangeDialog.setOnSave(record -> {
                recordService.save(record);
                loadItems();
            });
            exchangeDialog.openCreate(exchange);
        });
        addMenu.addItem("Transfer", event -> {
            Transfer transfer = Transfer.create();
            TransferDialog transferDialog = new TransferDialog(context);
            transferDialog.setOnSave(record -> {
                recordService.save(record);
                loadItems();
            });
            transferDialog.openCreate(transfer);
        });
        contextMenu.addItem("Edit", event -> {
            event.getItem().ifPresent(record -> {
                switch (record) {
                    case Expense e -> {
                        ExpenseDialog dialog = new ExpenseDialog(context);
                        dialog.setOnSave(r -> {
                            recordService.save(r);
                            loadItems();
                        });
                        dialog.openUpdate(e.clone());
                    }
                    case Income i -> {
                        IncomeDialog dialog = new IncomeDialog(context);
                        dialog.setOnSave(r -> {
                            recordService.save(r);
                            loadItems();
                        });
                        dialog.openUpdate(i.clone());
                    }
                    case Exchange ex -> {
                        ExchangeDialog exchangeDialog = new ExchangeDialog(context);
                        exchangeDialog.setOnSave(r -> {
                            recordService.save(r);
                            loadItems();
                        });
                        exchangeDialog.openUpdate(ex.clone());
                    }
                    case Transfer tr -> {
                        TransferDialog transferDialog = new TransferDialog(context);
                        transferDialog.setOnSave(r -> {
                            recordService.save(r);
                            loadItems();
                        });
                        transferDialog.openUpdate(tr.clone());
                    }
                }
            });
        });
        contextMenu.addItem("Delete", event -> event.getItem().ifPresent(recordService::delete));
    }

    private HorizontalLayout createPaginationBar() {
        totalField.setReadOnly(true);
        pageField.setReadOnly(true);
        pagesField.setReadOnly(true);

        prevPageButton.addClickListener(event -> {
            currentPage--;
            loadItems();
        });
        nextPageButton.addClickListener(event -> {
            currentPage++;
            loadItems();
        });
        firstPageButton.addClickListener(event -> {
            currentPage = 1;
            loadItems();
        });
        lastPageButton.addClickListener(event -> {
            currentPage = totalPages;
            loadItems();
        });

        HorizontalLayout paginationBar = new HorizontalLayout(totalField, pagesField, firstPageButton, prevPageButton, pageField, nextPageButton, lastPageButton);
        paginationBar.setAlignItems(Alignment.END);
        return paginationBar;
    }

    private HorizontalLayout createFilterBar() {
        afterDatePicker.setLabel("After");
        beforeDatePicker.setLabel("Before");

        recordTypeSelect.setLabel("Type");
        recordTypeSelect.setItems(RecordType.values());
        recordTypeSelect.setEmptySelectionAllowed(true);
        recordTypeSelect.setEmptySelectionCaption("<Any>");
        recordTypeSelect.setItemLabelGenerator(type -> type != null ? type.name() : "<Any>");

        accountSelect.setLabel("Account");
        accountSelect.setItems(context.getActiveAccounts());
        accountSelect.setEmptySelectionAllowed(true);
        accountSelect.setEmptySelectionCaption("<Any>");
        accountSelect.setItemLabelGenerator(account -> account != null ? account.getName() : "<Any>");

        categorySelect.setLabel("Category");
        categorySelect.setItems(context.categories());
        categorySelect.setItemLabelGenerator(Category::getName);

        currencySelect.setLabel("Currency");
        currencySelect.setItems(context.getActiveCurrencies());
        currencySelect.setEmptySelectionAllowed(true);
        currencySelect.setEmptySelectionCaption("<Any>");
        currencySelect.setItemLabelGenerator(currency -> currency != null ? currency.getName() : "<Any>");

        tagSelect.setLabel("Tag");
        tagSelect.setItems(context.tags());
        tagSelect.setEmptySelectionAllowed(true);
        tagSelect.setEmptySelectionCaption("<Any>");
        tagSelect.setItemLabelGenerator(tag -> tag != null ? tag.getName() : "<Any>");


        Button filterButton = new Button("Filter");
        filterButton.addClickListener(event -> {
            filter = getFilter();
            currentPage = 1;
            loadItems();
        });
        Button resetButton = new Button("Reset");
        resetButton.addClickListener(event -> {
            resetFilter();
            currentPage = 1;
            loadItems();
        });

        HorizontalLayout filterBar = new HorizontalLayout(
                afterDatePicker, beforeDatePicker, recordTypeSelect, accountSelect, categorySelect, currencySelect, tagSelect,
                filterButton, resetButton);
        filterBar.setAlignItems(Alignment.END);
        return filterBar;
    }

    private void loadItems() {
        var page = recordService.getPage(currentPage - 1, pageSize, filter);
        recordList.setItems(page.getContent());
        totalItems = (int) page.getTotalElements();
        totalPages = Math.ceilDiv(totalItems, pageSize);
        pageField.setValue(currentPage);
        totalField.setValue(totalItems);
        pagesField.setValue(totalPages);
        prevPageButton.setEnabled(currentPage != 1);
        firstPageButton.setEnabled(currentPage != 1);
        nextPageButton.setEnabled(currentPage != totalPages);
        lastPageButton.setEnabled(currentPage != totalPages);
    }

    private static String getRecordAccounts(Record record) {
        return switch (record) {
            case Expense exp -> exp.getAccount().getName();
            case Income inc -> inc.getAccount().getName();
            case Exchange exch -> exch.getAccount().getName();
            case Transfer tr -> tr.getFromAccount().getName() + " -> " + tr.getToAccount().getName();
        };
    }

    private static String getRecordTypeTooltip(Record record) {
        return record.getClass().getSimpleName();
    }

    private static String getRecordAmount(Record record) {
        return switch (record) {
            case Expense exp -> exp.getAmount().toString();
            case Income inc -> inc.getAmount().toString();
            case Exchange exch -> String.format("%s -> %s", exch.getFromAmount(), exch.getToAmount());
            case Transfer tr -> tr.getAmount().toString();
        };
    }

    private static String getRecordTags(Record record) {
        return switch (record) {
            case Expense exp -> exp.getEntries().stream().flatMap(entry -> entry.getTags().stream()).distinct().map(Tag::getName).collect(Collectors.joining(", "));
            case Income inc -> inc.getTags().stream().map(Tag::getName).collect(Collectors.joining(", "));
            case Exchange exch -> "";
            case Transfer tr -> "";
        };
    }

    private RecordFilter getFilter() {
        return new RecordFilter(
                afterDatePicker.getValue(),
                beforeDatePicker.getValue(),
                recordTypeSelect.getValue(),
                accountSelect.getValue(),
                categorySelect.getValue(),
                currencySelect.getValue(),
                tagSelect.getValue());
    }

    private void resetFilter() {
        filter = null;
        afterDatePicker.clear();
        beforeDatePicker.clear();
        recordTypeSelect.clear();
        accountSelect.clear();
        categorySelect.clear();
        currencySelect.clear();
        tagSelect.clear();
    }
}
