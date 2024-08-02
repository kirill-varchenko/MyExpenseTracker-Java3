package com.example.myexpensetracker.view;

import com.example.myexpensetracker.domain.model.Currency;
import com.example.myexpensetracker.services.CurrencyService;
import com.example.myexpensetracker.view.components.BooleanIconValueProvider;
import com.example.myexpensetracker.view.dialogs.SimpleCreateUpdateDialog;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Route(value = "currencies", layout = MainView.class)
@PermitAll
public class CurrencyView extends VerticalLayout {
    private CurrencyService currencyService;

    private Grid<Currency> currencyList = new Grid<>();

    private List<Currency> currencies = new ArrayList<>();

    @Autowired
    public CurrencyView(CurrencyService currencyService) {
        this.currencyService = currencyService;

        currencyList.addColumn(Currency::getName).setHeader("Name");
        currencyList.addColumn(Currency::getCode).setHeader("Code");
        currencyList.addColumn(Currency::getSymbol).setHeader("Symbol");
        currencyList.addComponentColumn(new BooleanIconValueProvider<>(Currency::isActive)).setHeader("Active");

        reloadItems();

        GridContextMenu<Currency> menu = currencyList.addContextMenu();
        menu.addItem("Create", event -> {
            CurrencyCreateUpdateDialog currencyDialog = new CurrencyCreateUpdateDialog();
            currencyDialog.setOnSave(currency -> {
                try {
                    currencyService.save(currency);
                    showNotification("Created");
                    reloadItems();
                } catch (Exception ex) {
                    log.error("Error while saving new currency: {}", ex.toString());
                    showNotification(ex);
                }
            });
            currencyDialog.openCreate(Currency.create("", null, null, (short) currencies.size()));
        });
        menu.addItem("Edit", event -> {
            event.getItem().ifPresent(currency -> {
                log.debug("Editing: {}", currency);
                CurrencyCreateUpdateDialog currencyDialog = new CurrencyCreateUpdateDialog();
                currencyDialog.setOnSave(curr -> {
                    try {
                        currencyService.save(curr);
                        showNotification("Updated");
                        reloadItems();
                    } catch (Exception ex) {
                        log.error("Error while saving updated currency: {}", ex.toString());
                        showNotification(ex);
                    }
                });
                currencyDialog.openUpdate(currency.clone());
            });
        });
        menu.addItem("Move up", event -> {
            event.getItem().ifPresent(currency -> {
                if (currency.getOrder() == 0) return;
                Currency prev = currencies.get(currency.getOrder() - 1);
                currency.swapOrders(prev);
                try {
                    currencyService.save(currency);
                    currencyService.save(prev);
                    showNotification("Updated");
                    reloadItems();
                } catch (Exception ex) {
                    log.error("Error while saving moved currencies: {}", ex.toString());
                    showNotification(ex);
                }
            });
        });
        menu.addItem("Move down", event -> {
            event.getItem().ifPresent(currency -> {
                if (currency.getOrder() == currencies.size() - 1) return;
                Currency next = currencies.get(currency.getOrder() + 1);
                currency.swapOrders(next);
                try {
                    currencyService.save(currency);
                    currencyService.save(next);
                    showNotification("Updated");
                    reloadItems();
                } catch (Exception ex) {
                    log.error("Error while saving moved currencies: {}", ex.toString());
                    showNotification(ex);
                }
            });
        });

        add(currencyList);
    }

    private void reloadItems() {
        currencies = currencyService.getAll();
        currencyList.setItems(currencies);
    }

    private static void showNotification(String message) {
        Notification notification = Notification.show(message, 5000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private static void showNotification(Exception ex) {
        Notification notification = Notification.show(ex.getMessage(), 5000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    private class CurrencyCreateUpdateDialog extends SimpleCreateUpdateDialog<Currency> {
        private TextField nameField = new TextField("Name");
        private TextField codeField = new TextField("Code");
        private TextField symbolField = new TextField("Symbol");
        private Checkbox activeBox = new Checkbox();

        public CurrencyCreateUpdateDialog() {
            super(Currency.class);

            activeBox.setLabel("Active");

            add(nameField, codeField, symbolField, activeBox);

            binder.forField(nameField).asRequired().bind("name");
            binder.forField(codeField).withValidator(
                    name -> name.length() <= 4,
                    "Code must contain at most 4 characters").bind("code");
            binder.forField(symbolField).withValidator(
                    name -> name.length() <= 1,
                    "Symbol must contain at most one character").bind("symbol");
            binder.forField(activeBox).bind("active");
        }

        @Override
        protected void initCreate(Currency currency) {
            activeBox.setValue(true);
            activeBox.setVisible(false);
        }

        @Override
        protected void initUpdate(Currency currency) {
            activeBox.setVisible(true);
        }
    }
}
