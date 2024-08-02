package com.example.myexpensetracker.persistence.repositories;


import com.example.myexpensetracker.persistence.entities.RunningTotalEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.UUID;

public interface RunningTotalRepository extends Repository<RunningTotalEntity, RunningTotalEntity.CompositeId> {
    @Query("select a from RunningTotalEntity a where a.id.userId = ?1")
    List<RunningTotalEntity> findById(UUID userId);
}
