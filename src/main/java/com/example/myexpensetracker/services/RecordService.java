package com.example.myexpensetracker.services;

import com.example.myexpensetracker.domain.RecordFilter;
import com.example.myexpensetracker.domain.model.Record;
import com.example.myexpensetracker.persistence.entities.RecordEntity;
import com.example.myexpensetracker.persistence.mappers.RecordMapper;
import com.example.myexpensetracker.persistence.repositories.RecordRepository;
import com.example.myexpensetracker.persistence.specifications.RecordSpecificationsFactory;
import com.example.myexpensetracker.security.AuthService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Log4j2
@Service
public class RecordService {
    @Autowired
    private RecordRepository repository;

    @Autowired
    private AuthService authService;

    @Autowired
    private RecordMapper mapper;

    public Page<Record> getPage(int pageNumber, int pageSize, RecordFilter filter) {
        log.debug("Loading records: page {}, size {}", pageNumber, pageSize);

        // Solution for pagination in memory warning
        Specification<RecordEntity> spec = RecordSpecificationsFactory.ownedByUser(authService.getAuthenticatedUser());
        if (filter != null) {
            Specification<RecordEntity> filterSpec = RecordSpecificationsFactory.applyFilter(filter);
            spec = spec.and(filterSpec);
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "date"));
        Page<UUID> idsPage = repository.findIdsBySpecAndPageable(spec, pageable);
        List<RecordEntity> records = repository.findByIdInOrderByDateDescCreatedAtDesc(idsPage.getContent());
        return new PageImpl<>(mapper.entitiesToDomain(records), idsPage.getPageable(), idsPage.getTotalElements());
    }

    @Transactional
    public void save(Record record) {
        log.info("Saving: {}", record);
        RecordEntity recordEntity = mapper.domainToEntity(record);
        recordEntity.setUser(authService.getAuthenticatedUser());
        repository.save(recordEntity);
    }

    public void delete(Record record) {
        log.info("Deleting: {}", record);
    }
}
