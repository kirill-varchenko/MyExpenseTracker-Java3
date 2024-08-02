package com.example.myexpensetracker.security;

import com.example.myexpensetracker.persistence.entities.UserEntity;
import com.example.myexpensetracker.services.ContextService;
import com.example.myexpensetracker.services.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import jakarta.servlet.ServletException;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class AuthService {
    private static final String LOGOUT_SUCCESS_URL = "/login";
    private static final String LOGIN_SUCCESS_URL = "/";

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    public UserEntity getAuthenticatedUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Object principal = context.getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            if (userDetails instanceof CustomUserDetails customUserDetails) {
                return customUserDetails.getUser();
            }
        }
        // Anonymous or no authentication.
        return null;
    }

    public UserEntity register(@NonNull String username, @NonNull String password) {
        log.info("Registering new user: {}", username);
        if (userService.usernameExists(username)) {
            log.info("Username exists: {}", username);
            throw new RuntimeException("Username exists");
        }
        String hashedPassword = passwordEncoder.encode(password);
        try {
            return userService.create(username, hashedPassword);
        } catch (DataIntegrityViolationException ex) {
            log.error("Exception while registering: {}", ex.toString());
            throw new RuntimeException("Registration failed");
        }
    }

    public void login(@NonNull String username, @NonNull String password) {
        log.debug("Starting login: {}", username);
        VaadinServletRequest request = VaadinServletRequest.getCurrent();
        if (request == null) {
            // This is in a background thread and we can't access the request to
            // log in the user
            return;
        }
        try {
            request.login(username, password);
            // change session ID to protect against session fixation
            request.getHttpServletRequest().changeSessionId();
        } catch (ServletException e) {
            log.error("Exception while login: {}", e.toString());
            throw new RuntimeException(e.getMessage());
        }
        UI.getCurrent().getPage().setLocation(LOGIN_SUCCESS_URL);
    }

    public void logout() {
        log.debug("Log out");
        UI.getCurrent().getPage().setLocation(LOGOUT_SUCCESS_URL);
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(
                VaadinServletRequest.getCurrent().getHttpServletRequest(),
                null,
                null);
    }
}
