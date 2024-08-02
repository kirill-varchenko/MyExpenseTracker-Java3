package com.example.myexpensetracker.view.dialogs;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import lombok.NonNull;

import java.util.function.Consumer;

/**
 * Dialog for evaluating string expression as double.
 */
public class CalculatorDialog extends Dialog {
    public CalculatorDialog(@NonNull Consumer<Double> valueConsumer) {
        setHeaderTitle("Calculator");

        DoubleEvaluator evaluator = new DoubleEvaluator();

        TextField inputField = new TextField();
        inputField.setLabel("Expression");

        add(inputField);

        Button okButton = new Button("Ok", e -> {
            try {
                String rawValue = inputField.getValue().replaceAll(",", ".");
                Double result = evaluator.evaluate(rawValue);
                valueConsumer.accept(result);
                close();
            } catch (IllegalArgumentException ex) {
                Notification notification = Notification.show(ex.getMessage(), 5000, Notification.Position.TOP_CENTER);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        okButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelButton = new Button("Cancel", e -> {
            close();
        });
        getFooter().add(cancelButton);
        getFooter().add(okButton);
    }
}
