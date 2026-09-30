package tech.oliver.expensegate.service;

import org.springframework.stereotype.Service;
import tech.oliver.expensegate.controller.dto.DecisionDto;
import tech.oliver.expensegate.entity.Expense;
import tech.oliver.expensegate.entity.ExpenseStatus;
import tech.oliver.expensegate.repository.ExpenseRepository;


@Service
public class DecisionService {

    private final ExpenseRepository expenseRepository;

    public DecisionService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public Expense decide(Long id, DecisionDto decision) {

        var expense = expenseRepository.findById(id).orElseThrow();

        if (expense.getStatus() != ExpenseStatus.SUBMITTED) {
            throw new IllegalArgumentException("Expense not in submitted status");
        }

        expense.setStatus(decision.decision());

        return expenseRepository.save(expense);
    }
}
