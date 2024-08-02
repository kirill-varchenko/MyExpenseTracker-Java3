package com.example.myexpensetracker.persistence.mappers;

import com.example.myexpensetracker.domain.model.Tag;
import com.example.myexpensetracker.persistence.entities.TagEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TagMapper {
    @Mapping(target = "user", ignore = true)
    TagEntity domainToEntity(Tag tag);

    Tag entityToDomain(TagEntity entity);

    List<Tag> entitiesToDomain(List<TagEntity> entities);
}
