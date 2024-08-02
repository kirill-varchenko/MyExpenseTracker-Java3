package com.example.myexpensetracker.persistence.repositories;

import com.example.myexpensetracker.persistence.entities.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, UUID> {
    List<TagEntity> findByUser_Id(UUID id);

    List<TagEntity> findByUser_IdAndActiveTrue(UUID id);
}
