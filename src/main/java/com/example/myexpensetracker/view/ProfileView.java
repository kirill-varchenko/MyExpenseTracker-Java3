package com.example.myexpensetracker.view;

import com.example.myexpensetracker.domain.model.Account;
import com.example.myexpensetracker.domain.model.Currency;
import com.example.myexpensetracker.domain.model.Profile;
import com.example.myexpensetracker.services.AccountService;
import com.example.myexpensetracker.services.CurrencyService;
import com.example.myexpensetracker.services.ProfileService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Log4j2
@Route(value = "profile", layout = MainView.class)
@PermitAll
public class ProfileView extends VerticalLayout {
    private Select<Currency> baseCurrencySelect = new Select<>();
    private Select<Currency> defaultCurrencySelect = new Select<>();
    private Select<Account> defaultAccountSelect = new Select<>();
    private Button saveButton = new Button("Save");

    private Binder<Profile> binder = new Binder<>(Profile.class);

    @Autowired
    public ProfileView(ProfileService profileService, CurrencyService currencyService, AccountService accountService) {
        List<Currency> currencies = currencyService.getAll();
        List<Account> accountEntities = accountService.getAll();

        baseCurrencySelect.setLabel("Base Currency");
        baseCurrencySelect.setItems(currencies);
        baseCurrencySelect.setEmptySelectionAllowed(true);
        baseCurrencySelect.setEmptySelectionCaption("<None>");
        baseCurrencySelect.setItemLabelGenerator(currency -> currency != null ? currency.getName() : "<None>");

        defaultCurrencySelect.setLabel("Default Currency");
        defaultCurrencySelect.setItems(currencies);
        defaultCurrencySelect.setEmptySelectionAllowed(true);
        defaultCurrencySelect.setEmptySelectionCaption("<None>");
        defaultCurrencySelect.setItemLabelGenerator(currency -> currency != null ? currency.getName() : "<None>");

        defaultAccountSelect.setLabel("Default Account");
        defaultAccountSelect.setItems(accountEntities);
        defaultAccountSelect.setEmptySelectionAllowed(true);
        defaultAccountSelect.setEmptySelectionCaption("<None>");
        defaultAccountSelect.setItemLabelGenerator(account -> account != null ? account.getName() : "<None>");

        add(baseCurrencySelect, defaultCurrencySelect, defaultAccountSelect, saveButton);

        binder.forField(baseCurrencySelect).bind("baseCurrency");
        binder.forField(defaultCurrencySelect).bind("defaultCurrency");
        binder.forField(defaultAccountSelect).bind("defaultAccount");

        Profile currentProfile = profileService.get();
        binder.setBean(new Profile(currentProfile.getBaseCurrency(), currentProfile.getDefaultCurrency(), currentProfile.getDefaultAccount()));

        saveButton.addClickListener(event -> {
            if (binder.validate().isOk()) {
                try {
                    profileService.save(binder.getBean());
                    showNotification("Updated");
                } catch (Exception ex) {
                    log.error("Error while saving profile: {}", ex.toString());
                    showNotification(ex);
                }
            }
        });
    }

    private static void showNotification(String message) {
        Notification notification = Notification.show(message, 5000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private static void showNotification(Exception ex) {
        Notification notification = Notification.show(ex.getMessage(), 5000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}
