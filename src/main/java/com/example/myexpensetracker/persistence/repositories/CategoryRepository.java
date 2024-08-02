package com.example.myexpensetracker.persistence.repositories;

import com.example.myexpensetracker.persistence.entities.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {
    List<CategoryEntity> findByUser_Id(UUID id);

    List<CategoryEntity> findByUser_IdAndActiveTrue(UUID id);
}
