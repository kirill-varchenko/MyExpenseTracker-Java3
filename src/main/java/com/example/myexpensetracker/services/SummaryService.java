package com.example.myexpensetracker.services;

import com.example.myexpensetracker.domain.model.AccountTotal;
import com.example.myexpensetracker.domain.model.MonthlyExpenseIncome;
import com.example.myexpensetracker.domain.model.RunningTotal;
import com.example.myexpensetracker.persistence.entities.AccountTotalEntity;
import com.example.myexpensetracker.persistence.entities.MonthlyExpenseIncomeEntity;
import com.example.myexpensetracker.persistence.entities.RunningTotalEntity;
import com.example.myexpensetracker.persistence.mappers.SummaryMapper;
import com.example.myexpensetracker.persistence.repositories.AccountTotalRepository;
import com.example.myexpensetracker.persistence.repositories.MonthlyExpenceIncomeRepository;
import com.example.myexpensetracker.persistence.repositories.RunningTotalRepository;
import com.example.myexpensetracker.security.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SummaryService {
    @Autowired
    private AccountTotalRepository accountTotalRepository;

    @Autowired
    private MonthlyExpenceIncomeRepository monthlyExpenceIncomeRepository;

    @Autowired
    private RunningTotalRepository runningTotalRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private ContextService contextService;

    public List<AccountTotal> getAccountTotals() {
        List<AccountTotalEntity> entities = accountTotalRepository.findById(authService.getAuthenticatedUser().getId());
        return SummaryMapper.mapAccountTotals(entities, contextService.get());
    }

    public List<MonthlyExpenseIncome> getMonthlyExpenseIncomes() {
        List<MonthlyExpenseIncomeEntity> entities = monthlyExpenceIncomeRepository.findById(authService.getAuthenticatedUser().getId());
        return SummaryMapper.mapMonthlyExpenseIncomes(entities, contextService.get());
    }

    public List<RunningTotal> getRunningTotals() {
        List<RunningTotalEntity> entities = runningTotalRepository.findById(authService.getAuthenticatedUser().getId());
        return SummaryMapper.mapRunningTotals(entities, contextService.get());
    }
}
