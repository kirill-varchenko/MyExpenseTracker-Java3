package com.example.myexpensetracker.persistence.repositories;

import com.example.myexpensetracker.persistence.entities.CurrencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CurrencyRepository extends JpaRepository<CurrencyEntity, UUID> {
    List<CurrencyEntity> findByUser_Id(UUID id);

    List<CurrencyEntity> findByUser_IdAndActiveTrue(UUID id);
}
