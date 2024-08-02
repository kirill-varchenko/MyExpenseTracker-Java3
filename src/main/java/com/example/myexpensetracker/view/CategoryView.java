package com.example.myexpensetracker.view;

import com.example.myexpensetracker.domain.model.Category;
import com.example.myexpensetracker.services.CategoryService;
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
@Route(value = "categories", layout = MainView.class)
@PermitAll
public class CategoryView extends VerticalLayout {
    private CategoryService categoryService;

    private TreeGrid<Category> categoryTree = new TreeGrid<>();

    private List<Category> categories;

    @Autowired
    public CategoryView(CategoryService categoryService) {
        this.categoryService = categoryService;

        categoryTree.addHierarchyColumn(Category::getName).setHeader("Name");
        categoryTree.addComponentColumn(new BooleanIconValueProvider<>(Category::isActive)).setHeader("Active");

        reloadItems();

        GridContextMenu<Category> menu = categoryTree.addContextMenu();
        menu.addItem("Create", event -> {
            CategoryCreateUpdateDialog categoryDialog = new CategoryCreateUpdateDialog();
            categoryDialog.setOnSave(category -> {
                try {
                    categoryService.save(category);
                    showNotification("Created");
                    reloadItems();
                } catch (Exception ex) {
                    log.error("Error while saving new category: {}", ex.toString());
                    showNotification(ex);
                }
            });
            categoryDialog.openCreate(Category.create("", null));
        });
        menu.addItem("Edit", event -> {
            event.getItem().ifPresent(category -> {
                log.debug("Editing: {}", category);
                CategoryCreateUpdateDialog categoryDialog = new CategoryCreateUpdateDialog();
                categoryDialog.setOnSave(cat -> {
                    try {
                        categoryService.save(cat);
                        showNotification("Updated");
                        reloadItems();
                    } catch (Exception ex) {
                        log.error("Error while saving updated category: {}", ex.toString());
                        showNotification(ex);
                    }
                });
                categoryDialog.openUpdate(category.clone());
            });
        });

        add(categoryTree);
    }

    private void reloadItems() {
        categories = categoryService.getAll();
        List<Category> rootCategories = categories.stream().filter(account -> account.getParent() == null).toList();
        categoryTree.setItems(rootCategories, Category::getChildren);
        categoryTree.expand(rootCategories);
    }

    private static void showNotification(String message) {
        Notification notification = Notification.show(message, 5000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private static void showNotification(Exception ex) {
        Notification notification = Notification.show(ex.getMessage(), 5000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    private class CategoryCreateUpdateDialog extends SimpleCreateUpdateDialog<Category> {
        private TextField nameField = new TextField("Name");
        private Select<Category> parentSelect = new Select<>();
        private Checkbox activeBox = new Checkbox();

        public CategoryCreateUpdateDialog() {
            super(Category.class);

            parentSelect.setLabel("Parent");
            parentSelect.setItems(categories);
            parentSelect.setEmptySelectionAllowed(true);
            parentSelect.setEmptySelectionCaption("<None>");
            parentSelect.setItemLabelGenerator(category -> category != null ? category.getName() : "<None>");
            activeBox.setLabel("Active");

            add(nameField, parentSelect, activeBox);

            binder.forField(nameField).asRequired().bind("name");
            binder.forField(parentSelect).bind("parent");
            binder.forField(activeBox).bind("active");

        }

        @Override
        protected void initCreate(Category category) {
            activeBox.setValue(true);
            activeBox.setVisible(false);
        }

        @Override
        protected void initUpdate(Category category) {
            activeBox.setVisible(true);
        }
    }
}
