package com.example.myexpensetracker.view;

import com.example.myexpensetracker.domain.model.Tag;
import com.example.myexpensetracker.services.TagService;
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
@Route(value = "tags", layout = MainView.class)
@PermitAll
public class TagView extends VerticalLayout {
    private TagService tagService;

    private Grid<Tag> tagList = new Grid<>();

    private List<Tag> tags = new ArrayList<>();

    @Autowired
    public TagView(TagService tagService) {
        this.tagService = tagService;

        tagList.addColumn(Tag::getName).setHeader("Name");
        tagList.addComponentColumn(new BooleanIconValueProvider<>(Tag::isActive)).setHeader("Active");

        reloadItems();

        GridContextMenu<Tag> menu = tagList.addContextMenu();
        menu.addItem("Create", event -> {
            TagCreateUpdateDialog tagDialog = new TagCreateUpdateDialog();
            tagDialog.setOnSave(tag -> {
                try {
                    tagService.save(tag);
                    showNotification("Created");
                    reloadItems();
                } catch (Exception ex) {
                    log.error("Error while saving new tag: {}", ex.toString());
                    showNotification(ex);
                }
            });
            tagDialog.openCreate(Tag.create(""));
        });
        menu.addItem("Edit", event -> {
            event.getItem().ifPresent(tag -> {
                log.debug("Editing: {}", tag);
                TagCreateUpdateDialog tagDialog = new TagCreateUpdateDialog();
                tagDialog.setOnSave(t -> {
                    try {
                        tagService.save(t);
                        showNotification("Updated");
                        reloadItems();
                    } catch (Exception ex) {
                        log.error("Error while saving updated tag: {}", ex.toString());
                        showNotification(ex);
                    }
                });
                tagDialog.openUpdate(tag.clone());
            });
        });

        add(tagList);
    }

    private void reloadItems() {
        tags = tagService.getAll();
        tagList.setItems(tags);
    }

    private static void showNotification(String message) {
        Notification notification = Notification.show(message, 5000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private static void showNotification(Exception ex) {
        Notification notification = Notification.show(ex.getMessage(), 5000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    private class TagCreateUpdateDialog extends SimpleCreateUpdateDialog<Tag> {
        private TextField nameField = new TextField("Name");
        private Checkbox activeBox = new Checkbox();

        public TagCreateUpdateDialog() {
            super(Tag.class);

            activeBox.setLabel("Active");

            add(nameField, activeBox);

            binder.forField(nameField).asRequired().bind("name");
            binder.forField(activeBox).bind("active");
        }

        @Override
        protected void initCreate(Tag tag) {
            activeBox.setValue(true);
            activeBox.setVisible(false);
        }

        @Override
        protected void initUpdate(Tag tag) {
            activeBox.setVisible(true);
        }
    }
}
