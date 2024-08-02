package com.example.myexpensetracker.persistence.mappers;

import com.example.myexpensetracker.domain.model.Category;
import com.example.myexpensetracker.persistence.entities.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {
    @Mapping(target = "user", ignore = true)
    CategoryEntity domainToEntity(Category category);

    @Mapping(target = "children", ignore = true)
    Category entityToDomain(CategoryEntity entity);

    List<Category> entitiesToDomain(List<CategoryEntity> entities);
}
