package com.example.myexpensetracker.view;

import com.example.myexpensetracker.persistence.entities.UserEntity;
import com.example.myexpensetracker.security.AuthService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;


@Log4j2
@Route("login")
@AnonymousAllowed
public class LoginView extends VerticalLayout {
    private TextField usernameField = new TextField("Username");
    private PasswordField passwordField = new PasswordField("Password");
    private Button registerButton = new Button("Register");
    private Button loginButton = new Button("Login");

    private Binder<UserEntity> binder = new Binder<>(UserEntity.class);

    @Autowired
    public LoginView(AuthService authService) {
        setSizeFull();

        HorizontalLayout hl = new HorizontalLayout(registerButton, loginButton);
        add(usernameField, passwordField, hl);
        setAlignItems(Alignment.CENTER);

        binder.forField(usernameField).asRequired().bind("username");
        binder.forField(passwordField).asRequired().bind("password");

        registerButton.addClickListener(event -> {
            if (binder.validate().isOk()) {
                try {
                    UserEntity userEntity = authService.register(usernameField.getValue(), passwordField.getValue());
                    authService.login(usernameField.getValue(), passwordField.getValue());
                } catch (RuntimeException ex) {
                    log.warn("Error while register: {}", ex.toString());
                    showError(ex);
                }
            }
        });
        loginButton.addClickListener(event -> {
            if (binder.validate().isOk()) {
                try {
                    authService.login(usernameField.getValue(), passwordField.getValue());
                } catch (RuntimeException ex) {
                    log.warn("Error while login: {}", ex.toString());
                    showError(ex);
                }
            }
        });
    }

    private static void showError(Exception ex) {
        Notification notification = Notification.show(ex.getMessage(), 5000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}
