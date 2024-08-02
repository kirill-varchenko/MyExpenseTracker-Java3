package com.example.myexpensetracker.persistence.specifications;

import com.example.myexpensetracker.domain.RecordFilter;
import com.example.myexpensetracker.persistence.entities.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RecordSpecificationsFactory {
    public static Specification<RecordEntity> ownedByUser(UserEntity user) {
        return (root, query, criteriaBuilder) -> {
            Join<RecordEntity, UserEntity> userJoin = root.join("user");
            return criteriaBuilder.equal(userJoin.get("id"), user.getId());
        };
    }

    public static Specification<RecordEntity> applyFilter(RecordFilter filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.afterDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("date"), filter.afterDate()));
            }

            if (filter.beforeDate() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("date"), filter.beforeDate()));
            }

            if (filter.recordType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), filter.recordType()));
            }

            Join<RecordEntity, EntryEntity> entriesJoin = root.join("entries");

            if (filter.account() != null) {
                Join<EntryEntity, AccountEntity> accountJoin = entriesJoin.join("account");
                predicates.add(criteriaBuilder.equal(accountJoin.get("id"), filter.account().getId()));
            }

            if (filter.category() != null) {
                Join<EntryEntity, CategoryEntity> categoryJoin = entriesJoin.join("category");
                predicates.add(criteriaBuilder.equal(categoryJoin.get("id"), filter.category().getId()));
            }

            if (filter.currency() != null) {
                Join<EntryEntity, CurrencyEntity> currencyJoin = entriesJoin.join("currency");
                predicates.add(criteriaBuilder.equal(currencyJoin.get("id"), filter.currency().getId()));
            }

            if (filter.tag() != null) {
                Join<EntryEntity, TagEntity> tagJoin = entriesJoin.join("tags");
                predicates.add(criteriaBuilder.equal(tagJoin.get("id"), filter.tag().getId()));
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
