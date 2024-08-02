package com.example.myexpensetracker.services;

import com.example.myexpensetracker.domain.model.Category;
import com.example.myexpensetracker.persistence.entities.CategoryEntity;
import com.example.myexpensetracker.persistence.mappers.CategoryMapper;
import com.example.myexpensetracker.persistence.repositories.CategoryRepository;
import com.example.myexpensetracker.security.AuthService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@Service
public class CategoryService {
    @Autowired
    private CategoryRepository repository;

    @Autowired
    private AuthService authService;

    @Autowired
    private CategoryMapper mapper;

    public List<Category> getAll() {
        log.debug("Loading categories");
        List<CategoryEntity> entities = repository.findByUser_Id(authService.getAuthenticatedUser().getId());
        List<Category> categories = mapper.entitiesToDomain(entities);
        packChildren(categories);
        return categories;
    }

    public void save(Category category) {
        log.debug("Saving: {}", category);
        CategoryEntity entity = mapper.domainToEntity(category);
        entity.setUser(authService.getAuthenticatedUser());
        repository.save(entity);
    }

    private void packChildren(List<Category> categories) {
        Map<UUID, Category> categoryMap = categories.stream().collect(Collectors.toMap(Category::getId, Function.identity()));
        for (Category account : categories) {
            if (account.getParent() != null) {
                categoryMap.get(account.getParent().getId()).getChildren().add(account);
            }
        }
    }
}
