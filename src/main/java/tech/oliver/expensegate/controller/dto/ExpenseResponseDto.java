package tech.oliver.expensegate.controller.dto;

import tech.oliver.expensegate.entity.Expense;

import java.math.BigDecimal;

public record ExpenseResponseDto(Long id,
                                 String title,
                                 BigDecimal amount,
                                 String department,
                                 String status) {

    public static ExpenseResponseDto from(Expense expense) {

        return new ExpenseResponseDto(expense.getId(),
                                      expense.getTitle(),
                                      expense.getAmount(),
                                      expense.getDepartment().name(),
                                      expense.getStatus().name());
    }
}
