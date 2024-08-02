package com.example.myexpensetracker.persistence.repositories;

import com.example.myexpensetracker.persistence.entities.RecordEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface RecordRepository extends JpaRepository<RecordEntity, UUID>, CustomRecordRepository {
    // HHH000104: firstResult/maxResults specified with collection fetch; applying in memory!
    // Two-step solution:
    // 1. Query paginated ids and count (2 queries)
    // 2. Query records by ids (1 query)
    @Query(value = "select r.id from RecordEntity r where r.user.id = ?1",
            countQuery = "select count(r.id) from RecordEntity r where r.user.id = ?1")
    Page<UUID> findsRelevantIds(UUID userId, Pageable pageable);

    @EntityGraph("graph.record")
    List<RecordEntity> findByIdInOrderByDateDescCreatedAtDesc(Collection<UUID> ids);

    // Remade with EntityGraph
//    @Query("""
//            select r from RecordEntity r
//            join fetch r.entries e
//            join fetch e.account
//            join fetch e.currency
//            left join fetch e.category
//            left join fetch e.tags
//            where r.id in ?1
//            order by r.date desc
//            """)
//    List<RecordEntity> findByIds(List<UUID> ids);

}
