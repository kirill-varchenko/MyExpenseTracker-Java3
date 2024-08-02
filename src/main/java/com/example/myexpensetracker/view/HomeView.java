package com.example.myexpensetracker.view;

import com.example.myexpensetracker.services.SummaryService;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route(value = "home", layout = MainView.class)
@PermitAll
public class HomeView extends Div {
    public HomeView(SummaryService service) {
        add(new H1("Home"));
        System.out.println(service.getAccountTotals());
    }
}
