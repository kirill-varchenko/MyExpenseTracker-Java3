package com.example.myexpensetracker.persistence.repositories;

import com.example.myexpensetracker.persistence.entities.RecordEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomRecordRepositoryImpl implements CustomRecordRepository {
    @Autowired
    private EntityManager entityManager;

    @Override
    public Page<UUID> findIdsBySpecAndPageable(Specification<RecordEntity> spec, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UUID> query = cb.createQuery(UUID.class);
        Root<RecordEntity> root = query.from(RecordEntity.class);

        // TODO: Not sure if I need to use distinct here.
        // ERROR: for SELECT DISTINCT, ORDER BY expressions must appear in select list
//        query.distinct(true);

        // Specification
        if (spec != null) {
            query.where(spec.toPredicate(root, query, cb));
        }

        // Select only ids
        query.select(root.get("id"));

        // Sort
        if (pageable.getSort().isSorted()) {
            List<Order> orders = new ArrayList<>();
            pageable.getSort().forEach(sortOrder -> {
                if (sortOrder.isAscending()) {
                    orders.add(cb.asc(root.get(sortOrder.getProperty())));
                } else {
                    orders.add(cb.desc(root.get(sortOrder.getProperty())));
                }
            });
            query.orderBy(orders);
        }

        TypedQuery<UUID> typedQuery = entityManager.createQuery(query);

        // Pagination
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<UUID> ids = typedQuery.getResultList();

        // Total count
        long total = getTotalCount(spec);

        return new PageImpl<>(ids, pageable, total);
    }

    private long getTotalCount(Specification<RecordEntity> spec) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<RecordEntity> countRoot = countQuery.from(RecordEntity.class);
        countQuery.select(cb.countDistinct(countRoot));
        if (spec != null) {
            countQuery.where(spec.toPredicate(countRoot, countQuery, cb));
        }
        return entityManager.createQuery(countQuery).getSingleResult();
    }
}
