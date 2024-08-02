package com.example.myexpensetracker.persistence.repositories;


import com.example.myexpensetracker.persistence.entities.MonthlyExpenseIncomeEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.UUID;

public interface MonthlyExpenceIncomeRepository extends Repository<MonthlyExpenseIncomeEntity, MonthlyExpenseIncomeEntity.CompositeId> {
    @Query("select a from MonthlyExpenseIncomeEntity a where a.id.userId = ?1")
    List<MonthlyExpenseIncomeEntity> findById(UUID userId);
}
