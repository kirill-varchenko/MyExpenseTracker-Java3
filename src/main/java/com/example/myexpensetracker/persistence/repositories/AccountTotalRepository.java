package com.example.myexpensetracker.persistence.repositories;


import com.example.myexpensetracker.persistence.entities.AccountTotalEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.UUID;

public interface AccountTotalRepository extends Repository<AccountTotalEntity, AccountTotalEntity.CompositeId> {
    @Query("select a from AccountTotalEntity a where a.id.userId = ?1")
    List<AccountTotalEntity> findById(UUID userId);

}
