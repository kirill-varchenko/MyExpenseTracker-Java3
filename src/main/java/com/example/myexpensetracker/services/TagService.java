package com.example.myexpensetracker.services;

import com.example.myexpensetracker.domain.model.Tag;
import com.example.myexpensetracker.persistence.entities.TagEntity;
import com.example.myexpensetracker.persistence.mappers.TagMapper;
import com.example.myexpensetracker.persistence.repositories.TagRepository;
import com.example.myexpensetracker.security.AuthService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
public class TagService {
    @Autowired
    private TagRepository repository;

    @Autowired
    private AuthService authService;

    @Autowired
    private TagMapper mapper;

    public List<Tag> getAll() {
        log.debug("Loading tags");
        List<TagEntity> entities = repository.findByUser_Id(authService.getAuthenticatedUser().getId());
        return mapper.entitiesToDomain(entities);
    }

    public void save(Tag tag) {
        log.debug("Saving: {}", tag);
        TagEntity entity = mapper.domainToEntity(tag);
        entity.setUser(authService.getAuthenticatedUser());
        repository.save(entity);
    }
}
