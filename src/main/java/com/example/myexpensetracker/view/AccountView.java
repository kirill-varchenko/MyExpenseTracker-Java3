package com.example.myexpensetracker.view;

import com.example.myexpensetracker.domain.model.Account;
import com.example.myexpensetracker.services.AccountService;
import com.example.myexpensetracker.view.components.BooleanIconValueProvider;
import com.example.myexpensetracker.view.dialogs.SimpleCreateUpdateDialog;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Log4j2
@Route(value = "accounts", layout = MainView.class)
@PermitAll
public class AccountView extends VerticalLayout {
    private AccountService accountService;

    private TreeGrid<Account> accountTree = new TreeGrid<>();

    private List<Account> accounts;

    @Autowired
    public AccountView(AccountService accountService) {
        this.accountService = accountService;

        accountTree.addHierarchyColumn(Account::getName).setHeader("Name");
        accountTree.addColumn(Account::getType).setHeader("Type");
        accountTree.addComponentColumn(new BooleanIconValueProvider<>(Account::isActive)).setHeader("Active");

        reloadItems();

        GridContextMenu<Account> menu = accountTree.addContextMenu();
        menu.addItem("Create", event -> {
            AccountCreateUpdateDialog accountDialog = new AccountCreateUpdateDialog();
            accountDialog.setOnSave(account -> {
                try {
                    accountService.save(account);
                    showNotification("Created");
                    reloadItems();
                } catch (Exception ex) {
                    log.error("Error while saving new account: {}", ex.toString());
                    showNotification(ex);
                }
            });
            accountDialog.openCreate(Account.create("", Account.Type.BANK, null));
        });
        menu.addItem("Edit", event -> {
            event.getItem().ifPresent(account -> {
                log.debug("Editing: {}", account);
                AccountCreateUpdateDialog accountDialog = new AccountCreateUpdateDialog();
                accountDialog.setOnSave(acc -> {
                    try {
                        accountService.save(acc);
                        showNotification("Updated");
                        reloadItems();
                    } catch (Exception ex) {
                        log.error("Error while saving updated account: {}", ex.toString());
                        showNotification(ex);
                    }
                });
                accountDialog.openUpdate(account.clone());
            });
        });

        add(accountTree);
    }

    private void reloadItems() {
        accounts = accountService.getAll();
        List<Account> rootAccounts = accounts.stream().filter(account -> account.getParent() == null).toList();
        accountTree.setItems(rootAccounts, Account::getChildren);
        accountTree.expand(rootAccounts);
    }

    private static void showNotification(String message) {
        Notification notification = Notification.show(message, 5000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private static void showNotification(Exception ex) {
        Notification notification = Notification.show(ex.getMessage(), 5000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    private class AccountCreateUpdateDialog extends SimpleCreateUpdateDialog<Account> {
        private TextField nameField = new TextField("Name");
        private Select<Account.Type> typeSelect = new Select<>();
        private Select<Account> parentSelect = new Select<>();
        private Checkbox activeBox = new Checkbox();

        public AccountCreateUpdateDialog() {
            super(Account.class);

            typeSelect.setLabel("Type");
            typeSelect.setItems(Account.Type.values());
            parentSelect.setLabel("Parent");
            parentSelect.setItems(accounts);
            parentSelect.setEmptySelectionAllowed(true);
            parentSelect.setEmptySelectionCaption("<None>");
            parentSelect.setItemLabelGenerator(account -> account != null ? account.getName() : "<None>");
            activeBox.setLabel("Active");

            add(nameField, typeSelect, parentSelect, activeBox);

            binder.forField(nameField).asRequired().bind("name");
            binder.forField(typeSelect).asRequired().bind("type");
            binder.forField(parentSelect).bind("parent");
            binder.forField(activeBox).bind("active");

        }

        @Override
        protected void initCreate(Account account) {
            activeBox.setValue(true);
            activeBox.setVisible(false);
        }

        @Override
        protected void initUpdate(Account account) {
            activeBox.setVisible(true);
        }
    }
}
