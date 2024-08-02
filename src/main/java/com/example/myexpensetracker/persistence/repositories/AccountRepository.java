package com.example.myexpensetracker.persistence.repositories;

import com.example.myexpensetracker.persistence.entities.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {
    List<AccountEntity> findByUser_Id(UUID id);

    List<AccountEntity> findByUser_IdAndActiveTrue(UUID id);

}
