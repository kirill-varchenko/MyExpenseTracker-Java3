package com.example.myexpensetracker.view;

import com.example.myexpensetracker.security.AuthService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "")
@PermitAll
public class MainView extends AppLayout {
    private AuthService authService;

    @Autowired
    public MainView(AuthService authService) {
        this.authService = authService;
        HorizontalLayout header = createHeader();
        SideNav nav = getSideNav();
        addToDrawer(nav);
        addToNavbar(header);
    }

    private HorizontalLayout createHeader() {
        HorizontalLayout header = new HorizontalLayout(new DrawerToggle());

        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setWidth("100%");

        TextField usernameField = new TextField();
        usernameField.setReadOnly(true);
        usernameField.setValue(authService.getAuthenticatedUser().getUsername());

        Button logoutButton = new Button("Logout", VaadinIcon.SIGN_OUT.create());
        logoutButton.addClickListener(event -> authService.logout());

        Div div = new Div(usernameField, logoutButton);

        header.add(div);
        return header;
    }

    private SideNav getSideNav() {
        SideNav sideNav = new SideNav();
        sideNav.addItem(
                new SideNavItem("Dashboard", DashboardView.class, VaadinIcon.DASHBOARD.create()),
                new SideNavItem("Records", RecordView.class, VaadinIcon.TABLE.create()),
                new SideNavItem("Accounts", AccountView.class, VaadinIcon.PIGGY_BANK_COIN.create()),
                new SideNavItem("Currencies", CurrencyView.class, VaadinIcon.MONEY.create()),
                new SideNavItem("Categories", CategoryView.class, VaadinIcon.ARCHIVES.create()),
                new SideNavItem("Tags", TagView.class, VaadinIcon.TAGS.create()),
                new SideNavItem("Profile", ProfileView.class, VaadinIcon.OPTIONS.create()));
        return sideNav;
    }
}
