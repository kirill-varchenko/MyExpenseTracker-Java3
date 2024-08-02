package com.example.myexpensetracker.persistence.repositories;

import com.example.myexpensetracker.persistence.entities.RecordEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public interface CustomRecordRepository {
    Page<UUID> findIdsBySpecAndPageable(Specification<RecordEntity> spec, Pageable pageable);
}
