package tech.oliver.expensegate.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import tech.oliver.expensegate.controller.dto.CreateExpenseDto;
import tech.oliver.expensegate.entity.Expense;
import tech.oliver.expensegate.entity.ExpenseStatus;
import tech.oliver.expensegate.repository.ExpenseRepository;
import tech.oliver.expensegate.repository.UserRepository;


@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseService(ExpenseRepository expenseRepository,
                          UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    public Expense create(CreateExpenseDto dto,
                          Authentication auth) {

        var user = userRepository.findByUsername(auth.getName()).orElseThrow();

        var expense = new Expense();

        expense.setTitle(dto.title());
        expense.setAmount(dto.amount());
        expense.setDepartment(user.getDepartment());
        expense.setOwner(user);
        expense.setStatus(ExpenseStatus.SUBMITTED);

        return expenseRepository.save(expense);
    }

    public Expense findById(Long id, Authentication auth) {
        return expenseRepository.findById(id).orElseThrow();
    }
}
